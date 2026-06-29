package com.rideshare.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published to Kafka topic {@code ride.matched} when a driver is successfully matched to a ride.
 * Consumed by the Ride Service to update the ride with the assigned driver details.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideMatchedEvent {

    /**
     * Unique identifier of the ride that was matched.
     */
    private String rideId;

    /**
     * Unique identifier of the rider who requested the ride.
     */
    private String riderId;

    /**
     * Unique identifier of the driver assigned to the ride.
     */
    private String driverId;

    /**
     * Latitude coordinate of the driver's current location at the time of matching.
     */
    private double driverLatitude;

    /**
     * Longitude coordinate of the driver's current location at the time of matching.
     */
    private double driverLongitude;

    /**
     * Distance from the driver to the pickup location in kilometers.
     */
    private double distanceToPickupInKm;
}
