package com.rideshare.userservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for document response.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    @Schema(description = "Unique identifier of the document", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "Unique identifier of the user who uploaded the document", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Type of the document", example = "DRIVERS_LICENSE")
    private String documentType;

    @Schema(description = "URL of the uploaded document file", example = "https://storage.example.com/documents/license.pdf")
    private String fileUrl;

    @Schema(description = "Original name of the uploaded file", example = "drivers_license.pdf")
    private String fileName;

    @Schema(description = "Size of the file in bytes", example = "1048576")
    private Long fileSize;

    @Schema(description = "MIME type of the uploaded file", example = "application/pdf")
    private String mimeType;

    @Schema(description = "Current verification status of the document", example = "APPROVED")
    private String verificationStatus;

    @Schema(description = "Reason for rejection if the document was rejected", example = "Image is blurry")
    private String rejectionReason;

    @Schema(description = "Timestamp when the document was verified", example = "2025-06-20T14:45:00")
    private LocalDateTime verifiedAt;

    @Schema(description = "Identifier of the admin who verified the document", example = "admin-001")
    private String verifiedBy;

    @Schema(description = "Expiry date of the document", example = "2028-12-31")
    private LocalDate expiryDate;

    @Schema(description = "Timestamp when the document was uploaded", example = "2025-06-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the document was last updated", example = "2025-06-20T14:45:00")
    private LocalDateTime updatedAt;
}