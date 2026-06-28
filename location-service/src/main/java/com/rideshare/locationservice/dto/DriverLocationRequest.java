package com.rideshare.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for driver location update requests. Contains the
 * driver's unique identifier and their current geographic coordinates. Used by
 * the driver's app to send location updates every ~3 seconds.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverLocationRequest {

    /**
     * Unique identifier of the driver. Must not be null or empty.
     */
    private String driverId;

    /**
     * Latitude coordinate of the driver's current location. Valid range: -90 to
     * 90 degrees.
     */
    private double latitude;

    /**
     * Longitude coordinate of the driver's current location. Valid range: -180
     * to 180 degrees.
     */
    private double longitude;
}
