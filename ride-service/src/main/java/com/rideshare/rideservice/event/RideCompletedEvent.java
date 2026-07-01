package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when a ride is successfully completed.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideCompletedEvent {

    private String rideId;
    private String riderId;
    private String driverId;
    private double actualFare;
    private double distanceKm;
    private double durationMinutes;
}
