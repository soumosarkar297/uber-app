package com.rideshare.tripservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Unique identifier of the trip record", example = "trip-abc123")
    private String id;

    @Schema(description = "ID of the associated ride", example = "ride-67890")
    private String rideId;

    @Schema(description = "ID of the rider", example = "user-12345")
    private String riderId;

    @Schema(description = "ID of the driver", example = "driver-98765")
    private String driverId;

    @Schema(description = "Full name of the driver", example = "John Smith")
    private String driverName;

    @Schema(description = "Pickup location address", example = "123 Main St, New York, NY")
    private String pickupAddress;

    @Schema(description = "Latitude coordinate of the pickup location", example = "40.7128")
    private double pickupLatitude;

    @Schema(description = "Longitude coordinate of the pickup location", example = "-74.0060")
    private double pickupLongitude;

    @Schema(description = "Dropoff location address", example = "456 Broadway, New York, NY")
    private String dropAddress;

    @Schema(description = "Latitude coordinate of the dropoff location", example = "40.7580")
    private double dropLatitude;

    @Schema(description = "Longitude coordinate of the dropoff location", example = "-73.9855")
    private double dropLongitude;

    @Schema(description = "Trip distance in kilometers", example = "12.5")
    private double distanceKm;

    @Schema(description = "Trip duration in minutes", example = "25.0")
    private double durationMinutes;

    @Schema(description = "Current status of the trip", example = "completed")
    private String status;

    @Schema(description = "Estimated fare for the trip", example = "22.50")
    private BigDecimal estimatedFare;

    @Schema(description = "Actual fare charged after trip completion", example = "24.00")
    private BigDecimal actualFare;

    @Schema(description = "Payment method used for the trip", example = "credit_card")
    private String paymentMethod;

    @Schema(description = "Surge pricing multiplier applied", example = "1.5")
    private double surgeMultiplier;

    @Schema(description = "Type of vehicle used for the trip", example = "sedan")
    private String vehicleType;

    @Schema(description = "Timestamp when the trip was requested")
    private LocalDateTime requestedAt;

    @Schema(description = "Timestamp when the trip started")
    private LocalDateTime startedAt;

    @Schema(description = "Timestamp when the trip was completed")
    private LocalDateTime completedAt;
}
