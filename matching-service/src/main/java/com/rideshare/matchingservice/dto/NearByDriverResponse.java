package com.rideshare.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a nearby driver with location and movement data.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {

    private String driverId;

    private double latitude;

    private double longitude;

    /** Distance from the search center in kilometers */
    private double distanceInKm;

    /** Driver's current heading in degrees (0-360) */
    private Double heading;

    /** Driver's current speed in km/h */
    private Double speed;
}
