package com.rideshare.driveronboardingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Verification decision (approved or rejected)", example = "approved", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Decision is required")
    private String decision;

    @Schema(description = "Reason for rejection, if applicable", example = "Image is blurry and unreadable")
    private String rejectionReason;

    @Schema(description = "ID of the admin who verified the document", example = "admin-001")
    private String verifiedBy;
}
