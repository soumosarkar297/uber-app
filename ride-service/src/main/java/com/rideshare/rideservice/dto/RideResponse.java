package com.rideshare.rideservice.dto;

import java.time.LocalDateTime;

import com.rideshare.rideservice.model.RideCancellationReason;
import com.rideshare.rideservice.model.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing the full state of a ride.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {

    private String id;
    private String riderId;
    private String driverId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
    private RideStatus status;
    private RideCancellationReason cancellationReason;
    private String cancelledBy;
    private double estimatedFare;
    private double actualFare;
    private Double distanceKm;
    private Double durationMinutes;
    private String vehicleType;
    private Double surgeMultiplier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime driverArrivedAt;
    private String paymentMethod;
}
