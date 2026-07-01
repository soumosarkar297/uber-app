package com.rideshare.userservice.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "File URL is required")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String fileUrl;

    @Size(max = 100, message = "File name must not exceed 100 characters")
    private String fileName;

    private Long fileSize;

    @Size(max = 50, message = "Mime type must not exceed 50 characters")
    private String mimeType;

    private String expiryDate;
}