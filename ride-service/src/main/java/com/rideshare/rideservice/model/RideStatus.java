package com.rideshare.rideservice.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Enumerates the possible statuses of a ride with enforced state transitions.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RideStatus {
    REQUESTED,
    MATCHING,
    ACCEPTED,
    DRIVER_ARRIVING,
    RIDE_STARTED,
    COMPLETED,
    CANCELLED;

    private static final Map<RideStatus, Set<RideStatus>> TRANSITIONS = new EnumMap<>(RideStatus.class);

    static {
        TRANSITIONS.put(REQUESTED, Set.of(MATCHING, CANCELLED));
        TRANSITIONS.put(MATCHING, Set.of(ACCEPTED, CANCELLED));
        TRANSITIONS.put(ACCEPTED, Set.of(DRIVER_ARRIVING, CANCELLED));
        TRANSITIONS.put(DRIVER_ARRIVING, Set.of(RIDE_STARTED, CANCELLED));
        TRANSITIONS.put(RIDE_STARTED, Set.of(COMPLETED));
        TRANSITIONS.put(COMPLETED, Set.of());
        TRANSITIONS.put(CANCELLED, Set.of());
    }

    public boolean canTransitionTo(RideStatus newStatus) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(newStatus);
    }

    public RideStatus validateTransition(RideStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid ride status transition: %s -> %s", this, newStatus));
        }
        return newStatus;
    }
}
