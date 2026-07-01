package com.rideshare.driveronboardingservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for uploaded document details.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private String id;
    private String driverId;
    private String applicationId;
    private String documentType;
    private String fileName;
    private String fileUrl;
    private String status;
    private String rejectionReason;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}
