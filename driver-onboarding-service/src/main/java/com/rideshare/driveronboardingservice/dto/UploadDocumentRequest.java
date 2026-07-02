package com.rideshare.driveronboardingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for uploading a verification document.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentRequest {

    @Schema(description = "Unique identifier of the driver uploading the document", example = "driver-98765", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @Schema(description = "ID of the onboarding application this document belongs to", example = "app-abc123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Application ID is required")
    private String applicationId;

    @Schema(description = "Type of document being uploaded (e.g., drivers_license, insurance, vehicle_registration)", example = "drivers_license", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Document type is required")
    private String documentType;

    @Schema(description = "Original file name of the uploaded document", example = "drivers_license_front.jpg")
    private String fileName;

    @Schema(description = "Storage URL of the uploaded document", example = "https://storage.example.com/docs/license.jpg")
    private String fileUrl;

    @Schema(description = "MIME type of the uploaded file", example = "image/jpeg")
    private String fileMimeType;

    @Schema(description = "Size of the uploaded file in bytes", example = "1048576")
    private Long fileSizeBytes;
}
