package com.example.housingsociety.controller;

import com.example.housingsociety.entity.User;
import com.example.housingsociety.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users/security")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSecurityContoller{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSecurityContoller(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Simple DTO for creation
    public static class SecurityUserCreateRequest {
        @NotBlank public String fullName;
        @NotBlank @Email public String email;
        @NotBlank @Size(min = 6, message = "Password must be at least 6 chars")
        public String password;
        @NotBlank public String contact;
    }

    /** Create a new SECURITY user (approved by default). */
    @PostMapping
    public User createSecurity(@RequestBody SecurityUserCreateRequest req) {
        userRepository.findByEmail(req.email).ifPresent(u -> {
            throw new RuntimeException("Email already exists");
        });

        User u = new User();
        u.setFullName(req.fullName);
        u.setEmail(req.email);
        u.setContact(req.contact);
        u.setPassword(passwordEncoder.encode(req.password));
        u.setRole(User.Role.SECURITY);   // ✅ Ensure your User.Role has 'security'
        u.setIsApproved(true);           // ✅ Admin-created → directly approved
        u.setCreatedAt(LocalDateTime.now());

        return userRepository.save(u);
    }

    /** List all SECURITY users. */
    @GetMapping
    public List<User> listSecurity() {
        return userRepository.findByRole(User.Role.SECURITY);
    }

    /** Delete a SECURITY user by id. */
    @DeleteMapping("/{id}")
    public void deleteSecurity(@PathVariable Integer id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (u.getRole() != User.Role.SECURITY) {
            throw new RuntimeException("Not a security user");
        }
        userRepository.deleteById(id);
    }
}
