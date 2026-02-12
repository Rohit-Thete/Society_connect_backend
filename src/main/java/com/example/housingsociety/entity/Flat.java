package com.example.housingsociety.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Flat {
    @Id
    @Column(length = 10)
    @NotBlank
    private String flatNo;

    @Column(length = 10)
    private String block;

    private Integer floor;

    private Integer sizeSqft;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}