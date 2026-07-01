package com.rideshare.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing estimated time of arrival calculations.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EtaResponse {

    /** Estimated time to pickup in minutes */
    private double pickupEtaMinutes;

    /** Estimated trip duration in minutes */
    private double tripDurationMinutes;

    /** Total estimated distance in km */
    private double totalDistanceKm;

    /** Current driver latitude */
    private double driverLatitude;

    /** Current driver longitude */
    private double driverLongitude;
}
