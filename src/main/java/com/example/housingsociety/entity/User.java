package com.example.housingsociety.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.example.housingsociety.entity.Flat;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Full name is required")
    @Pattern(regexp = "^[A-Za-z\s]+$", message = "Name must contain only letters and spaces")
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Password is required")
    private String password;

    @Column(length = 10)
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact must be 10 digits")
    private String contact;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean isApproved = false;

    private LocalDateTime createdAt;

    public enum Role { ADMIN, SECURITY, RESIDENT }
    @Column(name="flat_number")
    private String flatNumber;


}
