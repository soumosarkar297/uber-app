package com.rideshare.tripservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.tripservice.event.AnalyticsEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer that listens to the analytics.events topic for real-time aggregation.
 * Processes ride, payment, and location events to update analytics counters and metrics.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private final AnalyticsAggregationService analyticsService;

    /**
     * Consumes analytics events and routes to the appropriate aggregation method.
     */
    @KafkaListener(topics = "analytics.events", groupId = "trip-service-analytics-group")
    public void consumeAnalyticsEvent(AnalyticsEvent event) {
        try {
            switch (event.getEventType()) {
                case "RIDE_COMPLETED" -> analyticsService.recordRideCompleted(
                        event.getRideId(), event.getDriverId(), event.getRiderId(),
                        event.getAmount(), event.getDistanceKm(),
                        event.getDurationMinutes(), event.getZone());

                case "RIDE_CANCELLED" -> analyticsService.recordRideCancelled(
                        event.getRideId(), event.getZone());

                case "LOCATION_UPDATED" -> analyticsService.recordLocationUpdate(
                        event.getDriverId(), event.getZone());

                default -> log.debug("Unknown analytics event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing analytics event: {} - {}", event.getEventType(), e.getMessage());
        }
    }

    /**
     * Consumes payment completed events for revenue tracking.
     */
    @KafkaListener(topics = "payment.completed", groupId = "trip-service-analytics-group")
    public void consumePaymentCompleted(com.rideshare.tripservice.event.RideEvent event) {
        // Revenue is already tracked via ride.completed, but this confirms payment
        log.debug("Payment confirmed for ride: {} amount: {}", event.getRideId(), event.getFare());
    }
}
