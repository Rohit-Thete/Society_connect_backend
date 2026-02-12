package com.example.housingsociety.controller;

import com.example.housingsociety.entity.MaintenanceBill;
import com.example.housingsociety.service.MaintainanceBillService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintainanceController {

    private final MaintainanceBillService svc;

    public MaintainanceController(MaintainanceBillService svc) {
        this.svc = svc;
    }

    /** ADMIN: Generate monthly bills for all active flats (₹2/sqft + carry forward dues). */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public Map<String, Object> generate(
            @RequestParam @Min(2000) @Max(3000) Integer year,
            @RequestParam @Min(1) @Max(12) Integer month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        var res = svc.generateMonthly(year, month, dueDate);
        svc.notifyAdmins(
                "[Society] Maintenance Generated",
                "Generated for " + month + "/" + year + ": created=" + res.created() + ", skipped=" + res.skipped()
        );
        return Map.of(
                "created", res.created(),
                "skipped", res.skipped(),
                "totalFlats", res.total()
        );
    }

    /** ADMIN: Send bill emails for a period (safe/no-op if mail disabled). */
    @PostMapping("/send-emails")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public Map<String, Object> sendEmails(@RequestParam Integer year, @RequestParam Integer month) {
        int sent = svc.sendBillEmails(year, month);
        return Map.of("emailsSent", sent);
    }

    /** ADMIN: List all bills for a period. */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public List<MaintenanceBill> listForPeriod(@RequestParam Integer year, @RequestParam Integer month) {
        return svc.listForPeriod(year, month);
    }

    /** ADMIN: List all due (unpaid/partial) bills. */
    @GetMapping("/due")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public List<MaintenanceBill> listAllDue() {
        return svc.listAllDue();
    }

    /** ADMIN/RESIDENT: List bills for a specific flat. */
    @GetMapping("/flat/{flatNo}")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN','RESIDENT','ROLE_RESIDENT')")
    public List<MaintenanceBill> listForFlat(@PathVariable String flatNo) {
        return svc.listForFlat(flatNo);
    }

    /** ADMIN/RESIDENT: All bills grouped by every flat linked to the logged-in user. */
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN','RESIDENT','ROLE_RESIDENT')")
    public Map<String, List<MaintenanceBill>> myBills(java.security.Principal principal) {
        String email = principal.getName();
        return svc.listForUser(email);
    }

    /** ADMIN: Record a payment against a bill (for gateway callback later). */
    @PostMapping("/{billId}/payment")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    public MaintenanceBill recordPayment(@PathVariable Integer billId, @RequestParam BigDecimal amount) {
        return svc.recordPayment(billId, amount);
    }
}
