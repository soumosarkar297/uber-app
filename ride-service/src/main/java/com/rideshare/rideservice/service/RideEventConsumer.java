package com.rideshare.rideservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.rideservice.event.RideMatchedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer that listens for {@code ride.matched} events and delegates
 * to the {@link RideService} for driver assignment updates.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final RideService rideService;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 500;

    /**
     * Listens to the {@code ride.matched} Kafka topic.
     * Triggered every time the Matching Service publishes a matched driver event.
     * <p>
     * Flow: Matching Service → Kafka (ride.matched) → This Consumer → RideService
     * <p>
     * Retries up to {@value MAX_RETRIES} times with increasing backoff when the ride
     * is not yet persisted (transient race condition between services).
     *
     * @param event the ride matched event containing ride and driver identifiers
     */
    @KafkaListener(
            topics = "ride.matched",
            groupId = "ride-service-group"
    )
    public void consumeRideMatchedEvent(RideMatchedEvent event) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                rideService.updateRideWithDriver(event.getRideId(), event.getDriverId());
                return;
            } catch (RuntimeException e) {
                if (attempt < MAX_RETRIES && e.getMessage().contains("Ride not found")) {
                    log.warn("Ride {} not found (attempt {}/{}), retrying in {}ms...",
                            event.getRideId(), attempt, MAX_RETRIES, RETRY_DELAY_MS * attempt);
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                } else {
                    log.error("Error processing ride matching: {} - {}", event.getRideId(), e.getMessage());
                    return;
                }
            }
        }
    }

}
