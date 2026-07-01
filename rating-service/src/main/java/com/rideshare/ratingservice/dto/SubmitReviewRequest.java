package com.rideshare.ratingservice.dto;

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

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotBlank(message = "Reviewer ID is required")
    private String reviewerId;

    @NotBlank(message = "Reviewee ID is required")
    private String revieweeId;

    @NotBlank(message = "Reviewer type is required")
    private String reviewerType;

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private int rating;

    private String comment;

    private String tags;

    private boolean anonymous;
}
