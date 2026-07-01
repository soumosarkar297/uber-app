package com.rideshare.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event payload representing a ride lifecycle event.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideEvent {

    private String rideId;
    private String riderId;
    private String driverId;
    private String pickupAddress;
    private String dropAddress;
    private double amount;
}
