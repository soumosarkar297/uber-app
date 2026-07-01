package com.rideshare.userservice.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a document uploaded by a user for verification.
 * Linked to a user (rider or driver) and tracked through verification status.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull(message = "Document type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 30)
    private DocumentType documentType;

    @NotBlank(message = "File URL is required")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Size(max = 100, message = "File name must not exceed 100 characters")
    @Column(name = "file_name", length = 100)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Size(max = 50, message = "Mime type must not exceed 50 characters")
    @Column(name = "mime_type", length = 50)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}