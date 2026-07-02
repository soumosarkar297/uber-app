package com.rideshare.ratingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing aggregated rating statistics for a user.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingAggregateResponse {

    @Schema(description = "Unique identifier of the user", example = "user-12345")
    private String userId;

    @Schema(description = "Average rating across all reviews", example = "4.7")
    private BigDecimal averageRating;

    @Schema(description = "Total number of ratings received", example = "128")
    private int totalRatings;

    @Schema(description = "Count of 5-star ratings", example = "80")
    private int fiveStarCount;

    @Schema(description = "Count of 4-star ratings", example = "30")
    private int fourStarCount;

    @Schema(description = "Count of 3-star ratings", example = "12")
    private int threeStarCount;

    @Schema(description = "Count of 2-star ratings", example = "4")
    private int twoStarCount;

    @Schema(description = "Count of 1-star ratings", example = "2")
    private int oneStarCount;
}
