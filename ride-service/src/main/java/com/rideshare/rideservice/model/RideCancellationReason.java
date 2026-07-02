package com.rideshare.rideservice.model;

/**
 * Enumerates the possible reasons a ride may be cancelled.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RideCancellationReason {
    RIDER_CANCELLED,
    DRIVER_CANCELLED,
    DRIVER_UNAVAILABLE,
    NO_DRIVERS_FOUND,
    NO_DRIVERS_AVAILABLE,
    NO_SUITABLE_DRIVER,
    MATCHING_TIMEOUT,
    SURGE_TOO_HIGH,
    TIMEOUT,
    SYSTEM_ERROR
}
