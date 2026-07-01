package com.rideshare.locationservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing surge pricing area information.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurgeAreaResponse {

    private String zone;

    private double centerLatitude;

    private double centerLongitude;

    private double radiusKm;

    /** Number of available drivers in the zone */
    private int driverCount;

    /** Number of ride requests in the zone */
    private int activeRideCount;

    /** Surge multiplier (1.0 = no surge, >1.0 = surge active) */
    private double surgeMultiplier;
}
