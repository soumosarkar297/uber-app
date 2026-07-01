package com.rideshare.tripservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a single trip record.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRecordResponse {

    private String id;
    private String rideId;
    private String riderId;
    private String driverId;
    private String driverName;
    private String pickupAddress;
    private double pickupLatitude;
    private double pickupLongitude;
    private String dropAddress;
    private double dropLatitude;
    private double dropLongitude;
    private double distanceKm;
    private double durationMinutes;
    private String status;
    private BigDecimal estimatedFare;
    private BigDecimal actualFare;
    private String paymentMethod;
    private double surgeMultiplier;
    private String vehicleType;
    private LocalDateTime requestedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
