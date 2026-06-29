package com.rideshare.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object representing a nearby driver response from the Location Service.
 * Contains driver identification, current location coordinates, and distance from the search center.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearByDriverResponse {

    /**
     * Unique identifier of the driver.
     */
    private String driverId;

    /**
     * Latitude coordinate of the driver's current location.
     */
    private double latitude;

    /**
     * Longitude coordinate of the driver's current location.
     */
    private double longitude;

    /**
     * Distance from the search center in kilometers.
     */
    private double distanceInKm;
}
