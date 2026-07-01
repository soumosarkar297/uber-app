package com.rideshare.driveronboardingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for an admin verification decision on a document.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyDocumentRequest {

    @NotBlank(message = "Decision is required")
    private String decision;

    private String rejectionReason;

    private String verifiedBy;
}
