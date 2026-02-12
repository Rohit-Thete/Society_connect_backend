package com.example.housingsociety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "maintenance_bills",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_maint_period_flat", columnNames = {"period_year", "period_month", "flat_no"})
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")           // <-- align with existing column
    private Integer id;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth; // 1..12

    @Column(name = "flat_no", length = 10, nullable = false)
    private String flatNo;

    @Column(name = "size_sqft")
    private Integer sizeSqft;

    @Column(name = "rate_per_sqft", precision = 12, scale = 2)
    private BigDecimal ratePerSqft;

    @Column(name = "current_amount", precision = 12, scale = 2)
    private BigDecimal currentAmount;

    @Column(name = "previous_dues", precision = 12, scale = 2)
    private BigDecimal previousDues;

    @Column(name = "total_due", precision = 12, scale = 2)
    private BigDecimal totalDue;

    public enum Status { PENDING, PARTIAL, PAID }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
