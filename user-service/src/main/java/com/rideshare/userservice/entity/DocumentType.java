package com.rideshare.userservice.entity;

/**
 * Enumeration of document types for verification.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DocumentType {
    // Rider documents
    PROFILE_PHOTO,
    GOVERNMENT_ID,
    PASSPORT,

    // Driver documents
    DRIVING_LICENSE,
    VEHICLE_REGISTRATION,
    VEHICLE_INSURANCE,
    VEHICLE_POLLUTION_CERTIFICATE,
    BACKGROUND_CHECK,
    MEDICAL_CERTIFICATE
}