package com.rideshare.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event consumed from Kafka topic {@code ride.requested} when a rider requests a new ride.
 * Published by the Ride Service and triggers the driver matching process.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestedEvent {

    /**
     * Unique identifier of the ride
     */
    private String rideId;

    /**
     * Unique identifier of the rider who requested the ride
     */
    private String riderId;

    /**
     * Latitude coordinate of the pickup location
     */
    private double pickupLatitude;

    /**
     * Longitude coordinate of the pickup location
     */
    private double pickupLongitude;

    /**
     * Human-readable address of the pickup location
     */
    private String pickupAddress;

    /**
     * Latitude coordinate of the drop-off location
     */
    private double dropLatitude;

    /**
     * Longitude coordinate of the drop-off location
     */
    private double dropLongitude;

    /**
     * Human-readable address of the drop-off location
     */
    private String dropAddress;
}
