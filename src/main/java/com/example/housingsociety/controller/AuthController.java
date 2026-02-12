package com.example.housingsociety.controller;

import com.example.housingsociety.dto.ApiResponse;
import com.example.housingsociety.dto.LoginRequest;
import com.example.housingsociety.dto.UserCreateRequest;
import com.example.housingsociety.entity.User;
import com.example.housingsociety.security.JwtUtil;
import com.example.housingsociety.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/auth", "/api/auth"})
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserCreateRequest req) {
        User user = User.builder()
                .fullName(req.fullName)
                .email(req.email.trim().toLowerCase())
                .password(req.password) // encoded in UserService.create(...)
                .contact(req.contact)
                .role(User.Role.valueOf(req.role.toUpperCase()))
                .isApproved(false)
                .flatNumber(req.flatNumber)
                .build();

        User saved = userService.create(user);
        return ResponseEntity.ok(new ApiResponse("Registered", true, saved.getUserId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        String email = req.email.trim().toLowerCase();

        var opt = userService.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }
        User user = opt.get();

        // Compare raw vs encoded password (BCrypt)
        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }

        // Optional: block unapproved residents
        if (user.getRole() == User.Role.RESIDENT && Boolean.FALSE.equals(user.getIsApproved())) {
            return ResponseEntity.status(403).body(Map.of("message", "Account pending admin approval"));
        }

        // Generate JWT (adapt role claim format to your JwtUtil implementation)
        String token = jwtUtil.generateToken(user.getEmail());

        // Return FLAT fields to match your Login.jsx expectations
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("role", user.getRole().name().toLowerCase()); // "admin" | "security" | "resident"
        body.put("email", user.getEmail());
        body.put("userId", user.getUserId());
        body.put("fullName", user.getFullName());
        body.put("isApproved", Boolean.TRUE.equals(user.getIsApproved()));

        return ResponseEntity.ok(body);
    }
}
