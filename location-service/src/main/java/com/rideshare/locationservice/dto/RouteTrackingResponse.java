package com.rideshare.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing route tracking summary for a ride.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteTrackingResponse {

    private String rideId;

    private String driverId;

    /** Total distance of the tracked route in km */
    private double totalDistanceKm;

    /** Average speed in km/h */
    private double averageSpeedKmh;

    /** Total number of route points recorded */
    private int pointCount;
}
