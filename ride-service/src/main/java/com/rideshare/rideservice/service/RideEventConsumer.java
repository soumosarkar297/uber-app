package com.rideshare.rideservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.rideservice.dto.RideCancelRequest;
import com.rideshare.rideservice.event.RideMatchedEvent;
import com.rideshare.rideservice.model.RideCancellationReason;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Consumes ride-related Kafka events and triggers corresponding state transitions.
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

    /** Consumes ride.matched events and accepts the ride with retry logic. */
    @KafkaListener(
            topics = "ride.matched",
            groupId = "ride-service-group"
    )
    public void consumeRideMatchedEvent(RideMatchedEvent event) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                rideService.acceptRide(event.getRideId(), event.getDriverId());
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
                    log.error("Error processing ride matching: {} - {}", event.getRideId(),
                            e.getMessage());
                    return;
                }
            }
        }
    }

    /** Consumes ride.declined events and cancels the ride accordingly. */
    @KafkaListener(
            topics = "ride.declined",
            groupId = "ride-service-group"
    )
    public void consumeRideDeclinedEvent(java.util.Map<String, String> event) {
        String rideId = event.get("rideId");
        String reason = event.getOrDefault("reason", "DRIVER_UNAVAILABLE");

        log.info("Ride {} declined by driver. Reason: {}", rideId, reason);

        try {
            rideService.cancelRide(rideId, new RideCancelRequest(
                    RideCancellationReason.valueOf(reason), "driver"));
        } catch (Exception e) {
            log.error("Error processing ride decline: {} - {}", rideId, e.getMessage());
        }
    }
}
