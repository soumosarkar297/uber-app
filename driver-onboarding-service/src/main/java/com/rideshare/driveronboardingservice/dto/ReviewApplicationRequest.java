package com.rideshare.driveronboardingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for an admin review decision on an onboarding application.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewApplicationRequest {

    @Schema(description = "Review decision for the application (approved or rejected)", example = "approved", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Decision is required")
    private String decision;

    @Schema(description = "Administrative notes or comments on the decision", example = "All requirements met")
    private String adminNotes;

    @Schema(description = "ID of the admin who reviewed the application", example = "admin-001")
    private String reviewedBy;
}
