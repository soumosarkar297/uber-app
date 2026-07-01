package com.rideshare.driveronboardingservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
 * JPA entity representing a driver onboarding application.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "onboarding_applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private String vehicleType;

    private String vehicleMake;

    private String vehicleModel;

    private Integer vehicleYear;

    private String vehicleColor;

    private String licensePlateNumber;

    private String licenseNumber;

    private LocalDateTime licenseExpiryDate;

    /** Background verification status */
    @Enumerated(EnumType.STRING)
    private VerificationStatus backgroundCheckStatus;

    private String backgroundCheckRef;

    /** Insurance details */
    private String insuranceProvider;

    private String insurancePolicyNumber;

    private LocalDateTime insuranceExpiryDate;

    /** Admin notes */
    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ApplicationStatus {
        DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, RESUBMIT_REQUIRED
    }

    public enum VerificationStatus {
        PENDING, IN_PROGRESS, VERIFIED, FAILED, EXPIRED
    }
}
