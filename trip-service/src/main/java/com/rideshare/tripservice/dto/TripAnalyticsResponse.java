package com.rideshare.tripservice.dto;

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

    private String userId;
    private int totalTrips;
    private double totalDistanceKm;
    private double totalDurationMinutes;
    private BigDecimal totalFare;
    private BigDecimal averageFare;
    private double averageRating;
    private int completedTrips;
    private int cancelledTrips;
}
