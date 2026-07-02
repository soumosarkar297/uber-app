package com.rideshare.tripservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing aggregated trip analytics for a user.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripAnalyticsResponse {

    @Schema(description = "Unique identifier of the user", example = "user-12345")
    private String userId;

    @Schema(description = "Total number of trips taken", example = "150")
    private int totalTrips;

    @Schema(description = "Total distance traveled across all trips in kilometers", example = "2500.75")
    private double totalDistanceKm;

    @Schema(description = "Total duration of all trips in minutes", example = "18000.0")
    private double totalDurationMinutes;

    @Schema(description = "Sum of fares across all trips", example = "4500.00")
    private BigDecimal totalFare;

    @Schema(description = "Average fare per trip", example = "30.00")
    private BigDecimal averageFare;

    @Schema(description = "Average rating given across all trips", example = "4.6")
    private double averageRating;

    @Schema(description = "Number of successfully completed trips", example = "140")
    private int completedTrips;

    @Schema(description = "Number of cancelled trips", example = "10")
    private int cancelledTrips;
}
