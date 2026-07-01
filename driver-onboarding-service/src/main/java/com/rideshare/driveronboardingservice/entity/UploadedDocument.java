package com.rideshare.driveronboardingservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a document uploaded by a driver for verification.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "uploaded_documents")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String driverId;

    @Column(nullable = false)
    private String applicationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    private String fileMimeType;

    private Long fileSizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    private String rejectionReason;

    private String verifiedBy;

    private LocalDateTime verifiedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum DocumentType {
        DRIVING_LICENSE, VEHICLE_REGISTRATION, VEHICLE_INSURANCE,
        POLLUTION_CERTIFICATE, PROFILE_PHOTO, VEHICLE_PHOTO,
        BANK_STATEMENT, ADDRESS_PROOF
    }

    public enum DocumentStatus {
        PENDING, APPROVED, REJECTED, EXPIRED
    }
}
