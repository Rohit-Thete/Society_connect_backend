package com.example.housingsociety.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Visitor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer visitorId;

    @Column(length = 100)
    @NotBlank
    private String name;

    @Column(length = 15)
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact must be 10 digits")
    private String contact;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flat_no", referencedColumnName = "flatNo", nullable = false)
    private Flat flat;

    private LocalDateTime inTime;

    private LocalDateTime outTime;

    @ManyToOne
    @JoinColumn(name = "logged_by")
    private User loggedBy;
}