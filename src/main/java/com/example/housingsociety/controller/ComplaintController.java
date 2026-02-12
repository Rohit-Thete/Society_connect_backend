package com.example.housingsociety.controller;

import com.example.housingsociety.entity.Complaint;
import com.example.housingsociety.entity.Complaint.Status; // import enum
import com.example.housingsociety.entity.User;
import com.example.housingsociety.repository.UserRepository;
import com.example.housingsociety.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService svc;
    private final UserRepository userRepository;

    public ComplaintController(ComplaintService svc, UserRepository userRepository) {
        this.svc = svc;
        this.userRepository = userRepository;
    }

    // Create complaint for the logged-in user (user taken from JWT)
    @PostMapping
    public Complaint create(@Valid @RequestBody Complaint c, Principal principal) {
        String email = extractEmailFromPrincipalOrSecurityContext(principal);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

        // Assign user & defaults
        c.setUser(user);
        if (c.getStatus() == null) {
            c.setStatus(Status.PENDING); // ✅ uses uppercase constant
        }
        if (c.getCreatedAt() == null) {
            c.setCreatedAt(LocalDateTime.now());
        }

        return svc.create(c);
    }

    // Admin: list all complaints
    @GetMapping
    public List<Complaint> list() {
        return svc.list();
    }

    // Admin: mark complaint resolved
    @PostMapping("/resolve/{id}")
    public Complaint resolve(@PathVariable Integer id) {
        return svc.resolve(id);
    }

    // Logged-in resident: get own complaints (no userId in URL)
    @GetMapping("/user")
    public List<Complaint> getForLoggedInUser(Principal principal) {
        String email = extractEmailFromPrincipalOrSecurityContext(principal);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

        return svc.getByUserId(user.getUserId());
    }

    // Helper to safely get email from security context if Principal is null or empty
    private String extractEmailFromPrincipalOrSecurityContext(Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        if (email == null || email.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                email = String.valueOf(auth.getName());
            }
        }
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Authenticated principal not found");
        }
        return email;
    }
}
