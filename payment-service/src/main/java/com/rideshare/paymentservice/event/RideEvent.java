package com.rideshare.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event payload for ride lifecycle events consumed from ride-service.
 * Used to trigger automatic payment processing on ride completion.
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
    private double actualFare;
    private double distanceKm;
    private double durationMinutes;
    private String paymentMethod;
}
