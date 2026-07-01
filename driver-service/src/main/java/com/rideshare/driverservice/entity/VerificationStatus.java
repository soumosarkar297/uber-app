package com.rideshare.driverservice.entity;

/**
 * Enumeration of verification statuses for drivers and documents.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public enum VerificationStatus {
    PENDING,
    IN_PROGRESS,
    VERIFIED,
    REJECTED,
    EXPIRED
}
