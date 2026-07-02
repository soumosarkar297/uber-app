package com.rideshare.rideservice.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.rideservice.dto.RideCancelRequest;
import com.rideshare.rideservice.event.RideDeclinedEvent;
import com.rideshare.rideservice.event.RideMatchedEvent;
import com.rideshare.rideservice.model.RideCancellationReason;

import lombok.extern.slf4j.Slf4j;

/**
 * Consumes ride-related Kafka events and triggers corresponding state transitions.
 * Uses a thread pool for non-blocking retry with exponential backoff.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
public class RideEventConsumer {

    private final RideService rideService;
    private final ExecutorService retryExecutor = Executors.newFixedThreadPool(4);

    private static final int MAX_RETRIES = 3;
    private static final long BASE_RETRY_DELAY_MS = 500;

    public RideEventConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    /**
     * Consumes ride.matched events and accepts the ride with async retry logic.
     * Retries up to 3 times with exponential backoff (500ms, 1000ms, 2000ms).
     */
    @KafkaListener(
            topics = "ride.matched",
            groupId = "ride-service-group"
    )
    public void consumeRideMatchedEvent(RideMatchedEvent event) {
        retryExecutor.submit(() -> retryAcceptRide(event, 1));
    }

    private void retryAcceptRide(RideMatchedEvent event, int attempt) {
        try {
            rideService.acceptRide(event.getRideId(), event.getDriverId());
        } catch (RuntimeException e) {
            if (attempt < MAX_RETRIES && e.getMessage() != null
                    && e.getMessage().contains("Ride not found")) {
                long delay = BASE_RETRY_DELAY_MS * attempt;
                log.warn("Ride {} not found (attempt {}/{}), retrying in {}ms...",
                        event.getRideId(), attempt, MAX_RETRIES, delay);
                retryExecutor.submit(() -> {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    retryAcceptRide(event, attempt + 1);
                });
            } else {
                log.error("Failed to process ride matching after retries for ride: {} - {}",
                        event.getRideId(), e.getMessage());
            }
        }
    }

    /**
     * Consumes ride.declined events and cancels the ride accordingly.
     */
    @KafkaListener(
            topics = "ride.declined",
            groupId = "ride-service-group"
    )
    public void consumeRideDeclinedEvent(RideDeclinedEvent event) {
        String rideId = event.getRideId();
        String reason = event.getReason() != null ? event.getReason() : "DRIVER_UNAVAILABLE";

        log.info("Ride {} declined. Reason: {}", rideId, reason);

        try {
            rideService.cancelRide(rideId, new RideCancelRequest(
                    RideCancellationReason.valueOf(reason), "system"));
        } catch (Exception e) {
            log.error("Error processing ride decline: {} - {}", rideId, e.getMessage());
        }
    }
}
