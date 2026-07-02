package com.rideshare.ratingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO representing a review submitted by a user.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    @Schema(description = "Unique identifier of the review", example = "review-abc123")
    private String id;

    @Schema(description = "ID of the ride associated with this review", example = "ride-67890")
    private String rideId;

    @Schema(description = "ID of the user who submitted the review", example = "user-12345")
    private String reviewerId;

    @Schema(description = "ID of the user being reviewed", example = "user-67890")
    private String revieweeId;

    @Schema(description = "Role of the reviewer (e.g., rider, driver)", example = "rider")
    private String reviewerType;

    @Schema(description = "Rating score from 1 to 5", example = "5")
    private int rating;

    @Schema(description = "Free-text comment for the review", example = "Great ride, very smooth!")
    private String comment;

    @Schema(description = "Comma-separated tags categorizing the review", example = "punctual,clean_car,friendly")
    private String tags;

    @Schema(description = "Whether the review is anonymous", example = "false")
    private boolean anonymous;

    @Schema(description = "Timestamp when the review was created")
    private LocalDateTime createdAt;
}
