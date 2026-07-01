package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when a ride is cancelled by a rider, driver, or system.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideCancelledEvent {

    private String rideId;
    private String riderId;
    private String driverId;
    private String cancellationReason;
    private String cancelledBy;
}
