package com.rideshare.rideservice.dto;

import java.time.LocalDateTime;

import com.rideshare.rideservice.model.RideCancellationReason;
import com.rideshare.rideservice.model.RideStatus;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Unique identifier of the ride", example = "ride-xyz-001")
    private String id;

    @Schema(description = "Unique identifier of the rider", example = "rider-abc-123")
    private String riderId;

    @Schema(description = "Unique identifier of the assigned driver", example = "driver-def-456")
    private String driverId;

    @Schema(description = "Latitude coordinate of the pickup location", example = "12.9716")
    private double pickupLatitude;

    @Schema(description = "Longitude coordinate of the pickup location", example = "77.5946")
    private double pickupLongitude;

    @Schema(description = "Human-readable address of the pickup location", example = "MG Road, Bengaluru")
    private String pickupAddress;

    @Schema(description = "Latitude coordinate of the drop location", example = "12.2958")
    private double dropLatitude;

    @Schema(description = "Longitude coordinate of the drop location", example = "76.6394")
    private double dropLongitude;

    @Schema(description = "Human-readable address of the drop location", example = "Mysuru Palace, Mysuru")
    private String dropAddress;

    @Schema(description = "Current status of the ride", example = "IN_PROGRESS")
    private RideStatus status;

    @Schema(description = "Reason for ride cancellation, if applicable")
    private RideCancellationReason cancellationReason;

    @Schema(description = "Identifier of the user who cancelled the ride", example = "rider-abc-123")
    private String cancelledBy;

    @Schema(description = "Estimated fare for the ride before completion", example = "350.00")
    private double estimatedFare;

    @Schema(description = "Final fare charged after ride completion", example = "320.50")
    private double actualFare;

    @Schema(description = "Total distance travelled in kilometres", example = "15.3")
    private Double distanceKm;

    @Schema(description = "Total duration of the ride in minutes", example = "25.5")
    private Double durationMinutes;

    @Schema(description = "Type of vehicle used for the ride", example = "SEDAN")
    private String vehicleType;

    @Schema(description = "Surge multiplier applied to the fare", example = "1.5")
    private Double surgeMultiplier;

    @Schema(description = "Timestamp when the ride was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update to the ride")
    private LocalDateTime updatedAt;

    @Schema(description = "Timestamp when the driver started the ride")
    private LocalDateTime startedAt;

    @Schema(description = "Timestamp when the ride was completed")
    private LocalDateTime completedAt;

    @Schema(description = "Timestamp when the ride was cancelled")
    private LocalDateTime cancelledAt;

    @Schema(description = "Timestamp when the driver accepted the ride request")
    private LocalDateTime acceptedAt;

    @Schema(description = "Timestamp when the driver arrived at the pickup location")
    private LocalDateTime driverArrivedAt;

    @Schema(description = "Payment method used for the ride", example = "WALLET")
    private String paymentMethod;
}
