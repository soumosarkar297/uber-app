package com.rideshare.driveronboardingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Unique identifier of the document record", example = "doc-abc123")
    private String id;

    @Schema(description = "ID of the driver who uploaded the document", example = "driver-98765")
    private String driverId;

    @Schema(description = "ID of the associated onboarding application", example = "app-abc123")
    private String applicationId;

    @Schema(description = "Type of document", example = "drivers_license")
    private String documentType;

    @Schema(description = "File name of the uploaded document", example = "drivers_license_front.jpg")
    private String fileName;

    @Schema(description = "Storage URL of the uploaded document", example = "https://storage.example.com/docs/license.jpg")
    private String fileUrl;

    @Schema(description = "Current verification status of the document", example = "pending")
    private String status;

    @Schema(description = "Reason for rejection, if applicable", example = "")
    private String rejectionReason;

    @Schema(description = "Timestamp when the document was verified")
    private LocalDateTime verifiedAt;

    @Schema(description = "Timestamp when the document was uploaded")
    private LocalDateTime createdAt;
}
