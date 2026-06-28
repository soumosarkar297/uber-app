package com.rideshare.rideservice.dto;

import java.time.LocalDateTime;

import com.rideshare.rideservice.model.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for ride response.
 * Contains complete ride information returned to clients.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {

    /** Unique identifier of the ride */
    private String id;

    /** Unique identifier of the rider who requested the ride */
    private String riderId;

    /** Unique identifier of the driver assigned to the ride (null if not matched) */
    private String driverId;

    /** Latitude coordinate of the pickup location */
    private double pickupLatitude;

    /** Longitude coordinate of the pickup location */
    private double pickupLongitude;

    /** Human-readable address of the pickup location */
    private String pickupAddress;

    /** Latitude coordinate of the drop-off location */
    private double dropLatitude;

    /** Longitude coordinate of the drop-off location */
    private double dropLongitude;

    /** Human-readable address of the drop-off location */
    private String dropAddress;

    /** Current status of the ride */
    private RideStatus status;

    /** Estimated fare calculated at ride creation */
    private double estimatedFare;

    /** Actual fare charged upon completion */
    private double actualFare;

    /** Timestamp when the ride was created */
    private LocalDateTime createdAt;

    /** Timestamp when the ride was last updated */
    private LocalDateTime updatedAt;

    /** Timestamp when the ride started (driver began trip) */
    private LocalDateTime startedAt;

    /** Timestamp when the ride was completed */
    private LocalDateTime completedAt;
}
