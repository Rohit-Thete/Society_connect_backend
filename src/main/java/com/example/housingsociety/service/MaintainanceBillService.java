package com.example.housingsociety.service;

import com.example.housingsociety.entity.Flat;
import com.example.housingsociety.entity.FlatUser;
import com.example.housingsociety.entity.MaintenanceBill;
import com.example.housingsociety.entity.MaintenanceBill.Status;
import com.example.housingsociety.entity.User;
import com.example.housingsociety.repository.FlatRepository;
import com.example.housingsociety.repository.FlatUserRepository;
import com.example.housingsociety.repository.MaintenanceBillRepository;
import com.example.housingsociety.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MaintainanceBillService {

    private final MaintenanceBillRepository billRepo;
    private final FlatRepository flatRepo;
    private final FlatUserRepository flatUserRepo;
    private final UserRepository userRepo;
    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from.address:society.noreply@example.com}")
    private String fromAddress;

    @Value("${app.mail.from.name:Society Office}")
    private String fromName;

    private static final BigDecimal RATE_PER_SQFT = new BigDecimal("2.00");

    public static record GenerateResult(int created, int skipped, int total) {}

    /** Generate bills for all active flats for a given (year, month). */
    public GenerateResult generateMonthly(Integer year, Integer month, LocalDate dueDate) {
        List<Flat> flats = flatRepo.findAll();
        int created = 0, skipped = 0;

        for (Flat f : flats) {
            if (Boolean.FALSE.equals(f.getIsActive())) {
                skipped++;
                continue;
            }
            String flatNo = f.getFlatNo();

            // Avoid duplicate for period+flat
            if (billRepo.findByPeriodYearAndPeriodMonthAndFlatNo(year, month, flatNo).isPresent()) {
                skipped++;
                continue;
            }

            int size = Optional.ofNullable(f.getSizeSqft()).orElse(0);
            BigDecimal currentAmount = RATE_PER_SQFT.multiply(BigDecimal.valueOf(size));

            // Sum previous unpaid/partial before this period
            BigDecimal previousDues = billRepo.findUnpaidBefore(flatNo, year, month)
                    .stream()
                    .map(b -> b.getTotalDue().subtract(Optional.ofNullable(b.getPaidAmount()).orElse(BigDecimal.ZERO)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal total = currentAmount.add(previousDues);

            MaintenanceBill bill = MaintenanceBill.builder()
                    .periodYear(year)
                    .periodMonth(month)
                    .flatNo(flatNo)
                    .sizeSqft(size)
                    .ratePerSqft(RATE_PER_SQFT)
                    .currentAmount(currentAmount)
                    .previousDues(previousDues)
                    .totalDue(total)
                    .status(Status.PENDING)
                    .dueDate(dueDate != null ? dueDate : lastDayOfMonth(year, month))
                    .generatedAt(LocalDateTime.now())
                    .build();

            billRepo.save(bill);
            created++;
        }

        return new GenerateResult(created, skipped, flats.size());
    }

    /** Send bill emails for the given period to each flat’s resident(s). Safe/no-op when mail disabled. */
    public int sendBillEmails(Integer year, Integer month) {
        List<MaintenanceBill> bills = billRepo.findByPeriodYearAndPeriodMonth(year, month);
        int sent = 0;
        for (MaintenanceBill b : bills) {
            List<String> recipients = findResidentEmailsForFlat(b.getFlatNo());
            if (recipients.isEmpty()) continue;
            if (safeSendMail(
                    recipients,
                    "[Society] Maintenance Bill - " + month + "/" + year + " - Flat " + b.getFlatNo(),
                    emailBodyForBill(b))) {
                sent++;
            }
        }
        return sent;
    }

    /** Optional: notify admin(s) with a summary. Safe/no-op when mail disabled. */
    public void notifyAdmins(String subject, String body) {
        List<User> admins = userRepo.findByRole(User.Role.ADMIN);
        List<String> emails = admins.stream().map(User::getEmail).filter(Objects::nonNull).toList();
        if (!emails.isEmpty()) {
            safeSendMail(emails, subject, body);
        }
    }

    public List<MaintenanceBill> listForPeriod(Integer year, Integer month) {
        return billRepo.findByPeriodYearAndPeriodMonth(year, month);
    }

    public List<MaintenanceBill> listForFlat(String flatNo) {
        return billRepo.findByFlatNoOrderByPeriodYearDescPeriodMonthDesc(flatNo);
    }

    public List<MaintenanceBill> listAllDue() {
        return billRepo.findByStatus(Status.PENDING);
    }

    /** Record a payment (for gateway callback later). */
    public MaintenanceBill recordPayment(Integer billId, BigDecimal amount) {
        MaintenanceBill b = billRepo.findById(billId).orElseThrow(() -> new RuntimeException("Bill not found"));
        BigDecimal already = Optional.ofNullable(b.getPaidAmount()).orElse(BigDecimal.ZERO);
        BigDecimal newPaid = already.add(amount);
        b.setPaidAmount(newPaid);
        b.setStatus(newPaid.compareTo(b.getTotalDue()) >= 0 ? Status.PAID : Status.PARTIAL);
        b.setUpdatedAt(LocalDateTime.now());
        return billRepo.save(b);
    }

    /** All bills for all flats linked to a user (by email), grouped by flatNo. */
    public Map<String, List<MaintenanceBill>> listForUser(String email) {
        var links = flatUserRepo.findByUser_Email(email);
        var flatNos = new LinkedHashSet<String>();
        for (FlatUser fu : links) {
            if (fu.getFlat() != null && fu.getFlat().getFlatNo() != null) {
                flatNos.add(fu.getFlat().getFlatNo());
            }
        }
        var result = new LinkedHashMap<String, List<MaintenanceBill>>();
        for (String flatNo : flatNos) {
            var bills = billRepo.findByFlatNoOrderByPeriodYearDescPeriodMonthDesc(flatNo);
            result.put(flatNo, bills);
        }
        return result;
    }

    // ---- helpers ----

    private LocalDate lastDayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1)
                .withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth());
    }

    private List<String> findResidentEmailsForFlat(String flatNo) {
        List<FlatUser> rows = flatUserRepo.findByFlat_FlatNo(flatNo);
        List<String> emails = new ArrayList<>();
        for (FlatUser fu : rows) {
            User u = fu.getUser();
            if (u != null && u.getRole() == User.Role.RESIDENT && u.getEmail() != null) {
                emails.add(u.getEmail());
            }
        }
        return emails;
    }

    /** Safe mail sender that never throws; respects app.mail.enabled. */
    private boolean safeSendMail(List<String> to, String subject, String text) {
        if (!mailEnabled || to == null || to.isEmpty()) return false;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(String.format("%s <%s>", fromName, fromAddress));
            msg.setTo(to.toArray(new String[0]));
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            return true;
        } catch (MailException ex) {
            System.err.println("Email send skipped/failed: " + ex.getMessage());
            return false;
        }
    }

    private String emailBodyForBill(MaintenanceBill b) {
        return """
                Dear Resident,

                Your maintenance bill for %d/%d has been generated.

                Flat: %s
                Rate: ₹%s per sq ft
                Size: %s sq ft
                Current Amount: ₹%s
                Previous Dues: ₹%s
                --------------------------------
                Total Due: ₹%s
                Due Date: %s

                You can view and pay your bill in the portal. (Pay Now coming soon.)

                Regards,
                Society Office
                """.formatted(
                b.getPeriodMonth(), b.getPeriodYear(),
                b.getFlatNo(),
                b.getRatePerSqft(), b.getSizeSqft(),
                b.getCurrentAmount(), b.getPreviousDues(),
                b.getTotalDue(),
                Optional.ofNullable(b.getDueDate()).map(LocalDate::toString).orElse("-")
        );
    }
}
