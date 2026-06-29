package com.rideshare.matchingservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.matchingservice.event.RideRequestedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer that listens for {@code ride.requested} events and delegates
 * to the {@link MatchingService} for driver assignment.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final MatchingService matchingService;

    /**
     * Listens to the {@code ride.requested} Kafka topic.
     * Triggered every time the Ride Service publishes a new ride request.
     * <p>
     * Flow: Ride Service → Kafka (ride.requested) → This Consumer → MatchingService
     *
     * @param event the ride request event containing pickup/dropoff details
     */
    @KafkaListener(
            topics = "ride.requested",
            groupId = "matching-service-group"
    )
    public void consumeRideRequestedEvent(RideRequestedEvent event) {
        try {
            matchingService.matchDriverForRide(event);
        } catch (Exception e) {
            log.error("Error processing ride request: {} - {}", event.getRideId(), e.getMessage());

            // In production: send to dead letter queue for retry
        }
    }
}
