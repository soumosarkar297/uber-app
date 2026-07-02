package com.rideshare.tripservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic analytics event that can represent any ride or payment lifecycle event
 * consumed from the analytics.events Kafka topic for real-time aggregation.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {

    private String eventType;
    private String rideId;
    private String riderId;
    private String driverId;
    private BigDecimal amount;
    private double distanceKm;
    private double durationMinutes;
    private String zone;
    private LocalDateTime timestamp;
}
