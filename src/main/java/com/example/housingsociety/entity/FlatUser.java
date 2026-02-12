package com.example.housingsociety.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "flat_users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FlatUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flat_no", referencedColumnName = "flatNo", nullable = false)
    private Flat flat;

    @Enumerated(EnumType.STRING)
    private ResidentType residentType = ResidentType.TENANT;

    private Boolean active = true;

    private LocalDate startDate;

    private LocalDate endDate;

    public enum ResidentType { OWNER, TENANT }
}