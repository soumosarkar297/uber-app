package com.rideshare.driveronboardingservice.dto;

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

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotBlank(message = "Application ID is required")
    private String applicationId;

    @NotBlank(message = "Document type is required")
    private String documentType;

    private String fileName;
    private String fileUrl;
    private String fileMimeType;
    private Long fileSizeBytes;
}
