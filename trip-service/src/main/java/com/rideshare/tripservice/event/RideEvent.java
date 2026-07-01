package com.rideshare.tripservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event payload representing a completed or cancelled ride.
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
    private double distanceKm;
    private double durationMinutes;
    private double fare;
    private String paymentMethod;
    private LocalDateTime requestedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
