package com.rideshare.rideservice.model;

/**
 * Enumeration representing the possible states of a ride lifecycle.
 * Flow: REQUESTED -> MATCHING -> ACCEPTED -> DRIVER_ARRIVING -> RIDE_STARTED -> COMPLETED
 * CANCELLED can occur at multiple stages.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RideStatus {
    /** Ride has been requested by rider, awaiting matching */
    REQUESTED,
    /** System is searching for a nearby driver */
    MATCHING,
    /** Driver has accepted the ride request */
    ACCEPTED,
    /** Driver is en route to pickup location */
    DRIVER_ARRIVING,
    /** Ride has started, driver is transporting rider */
    RIDE_STARTED,
    /** Ride has been completed successfully */
    COMPLETED,
    /** Ride was cancelled before completion */
    CANCELLED
}
