package com.rideshare.ratingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting a review for a rider or driver.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewRequest {

    @Schema(description = "Unique identifier of the ride being reviewed", example = "ride-67890", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @Schema(description = "ID of the user submitting the review", example = "user-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Reviewer ID is required")
    private String reviewerId;

    @Schema(description = "ID of the user being reviewed", example = "user-67890", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Reviewee ID is required")
    private String revieweeId;

    @Schema(description = "Role of the reviewer (e.g., rider, driver)", example = "rider", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Reviewer type is required")
    private String reviewerType;

    @Schema(description = "Rating score from 1 to 5", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private int rating;

    @Schema(description = "Free-text comment for the review", example = "Great ride, very smooth!")
    private String comment;

    @Schema(description = "Comma-separated tags categorizing the review", example = "punctual,clean_car,friendly")
    private String tags;

    @Schema(description = "Whether the review should remain anonymous", example = "false")
    private boolean anonymous;
}
