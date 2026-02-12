package com.example.housingsociety.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private MaintenanceBill bill;

    @Column(length = 50)
    private String paymentMode;

    @Column(length = 100)
    private String transactionId;

    private LocalDateTime paymentDate;
}