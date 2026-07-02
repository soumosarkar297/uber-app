package com.rideshare.locationservice.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event published when a driver's location is updated.
 * Consumed by analytics pipeline for real-time location tracking.
 *
 * Topic: location.updated
 * Key: driverId
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdatedEvent {

    private String driverId;
    private double latitude;
    private double longitude;
    private Double heading;
    private Double speed;
    private String zone;
    private LocalDateTime timestamp;
}
