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

    /**
     * Listens to the {@code ride.matched} Kafka topic.
     * Triggered every time the Matching Service publishes a matched driver event.
     * <p>
     * Flow: Matching Service → Kafka (ride.matched) → This Consumer → RideService
     *
     * @param event the ride matched event containing ride and driver identifiers
     */
    @KafkaListener(
            topics = "ride.matched",
            groupId = "ride-service-group"
    )
    public void consumeRideMatchedEvent(RideMatchedEvent event) {
        try {
            rideService.updateRideWithDriver(event.getRideId(), event.getDriverId());
        } catch (Exception e) {
            log.error("Error processing ride matching: {} - {}", event.getRideId(), e.getMessage());
        }
    }

}
