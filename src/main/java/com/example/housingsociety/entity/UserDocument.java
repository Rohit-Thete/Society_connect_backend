package com.example.housingsociety.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer docId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocType docType;

    @Column(length = 50)
    private String docNumber;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "File path is required")
    private String filePath;

    private LocalDateTime uploadedAt;

    private Boolean verified = false;

    public enum DocType { AADHAR, PAN, PASSPORT, DRIVING_LICENSE, OTHER }
}