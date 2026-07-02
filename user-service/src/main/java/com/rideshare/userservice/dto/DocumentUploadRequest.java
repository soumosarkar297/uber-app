package com.rideshare.userservice.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for document upload request.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadRequest {

    @Schema(description = "Unique identifier of the user uploading the document", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "User ID is required")
    private UUID userId;

    @Schema(description = "Type of the document being uploaded", example = "DRIVERS_LICENSE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Document type is required")
    private String documentType;

    @Schema(description = "URL of the uploaded document file", example = "https://storage.example.com/documents/license.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "File URL is required")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String fileUrl;

    @Schema(description = "Original name of the uploaded file", example = "drivers_license.pdf")
    @Size(max = 100, message = "File name must not exceed 100 characters")
    private String fileName;

    @Schema(description = "Size of the file in bytes", example = "1048576")
    private Long fileSize;

    @Schema(description = "MIME type of the uploaded file", example = "application/pdf")
    @Size(max = 50, message = "Mime type must not exceed 50 characters")
    private String mimeType;

    @Schema(description = "Expiry date of the document in YYYY-MM-DD format", example = "2028-12-31")
    private String expiryDate;
}