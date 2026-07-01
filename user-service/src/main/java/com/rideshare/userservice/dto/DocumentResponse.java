package com.rideshare.userservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    private UUID id;
    private UUID userId;
    private String documentType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String verificationStatus;
    private String rejectionReason;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}