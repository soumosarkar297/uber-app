package com.rideshare.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a geographic zone with active surge pricing.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurgeArea {

    private String zone;

    private double centerLatitude;

    private double centerLongitude;

    private double radiusKm;

    private int driverCount;

    private int activeRideCount;

    /** Surge multiplier (1.0 = no surge, >1.0 = surge active) */
    private double surgeMultiplier;
}
