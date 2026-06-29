package com.rideshare.matchingservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.matchingservice.client.LocationServiceClient;
import com.rideshare.matchingservice.dto.NearByDriverResponse;
import com.rideshare.matchingservice.event.RideMatchedEvent;
import com.rideshare.matchingservice.event.RideRequestedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Core service responsible for matching riders with nearby drivers.
 * Coordinates with the Location Service to find available drivers and publishes
 * matched events to Kafka for downstream processing.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS_IN_KM = 5.0;

    /**
     * Main matching algorithm invoked when a {@link RideRequestedEvent} is consumed from Kafka.
     * Queries the Location Service for nearby drivers, scores them, and publishes a
     * {@link RideMatchedEvent} with the best match.
     *
     * @param event the ride request event containing pickup location and rider details
     * @throws RuntimeException if the Location Service is unavailable or Kafka send fails
     */
    public void matchDriverForRide(RideRequestedEvent event) {

        // STEP 1: Ask Location Service for nearby drivers
        List<NearByDriverResponse> nearByDrivers = locationServiceClient.getNearByDrivers(
                event.getPickupLatitude(), event.getPickupLongitude(), DEFAULT_SEARCH_RADIUS_IN_KM);

        if (nearByDrivers.isEmpty()) {
            log.warn("No drivers found near ride: {}", event.getRideId());
            return;
        }

        // STEP 2: Score each driver and pick the best one
        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearByDrivers);

        if (bestDriver.isEmpty()) {
            log.warn("Could not find suitable driver for ride: {}", event.getRideId());
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get();

        // STEP 3: Publish RideMatchedEvent to Kafka
        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm());

        kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), matchedEvent);
        log.info("RideMatchedEvent published for ride: {}", event.getRideId());
    }

    /**
     * Driver scoring algorithm that selects the best driver based on distance and rating.
     * <p>
     * Scoring formula: {@code Score = (1 / distance) * distanceWeight + rating * ratingWeight}
     * <ul>
     *   <li>Distance weight: 70% (closer drivers score higher)</li>
     *   <li>Rating weight: 30% (higher rated drivers score higher)</li>
     * </ul>
     * In production, the rating should be fetched from the Driver Service.
     *
     * @param drivers list of nearby drivers to score
     * @return an {@link Optional} containing the best driver, or empty if no drivers provided
     */
    private Optional<NearByDriverResponse> findBestDriver(
            List<NearByDriverResponse> drivers
    ) {
        double distanceWeight = 0.7;
        double ratingWeight = 0.3;

        return drivers.stream()
                .max(Comparator.comparingDouble(driver -> {
                    // Distance score: closer = higher score
                    // Add 0.1 to avoid division by zero

                    double distanceScore = 1.0 / (driver.getDistanceInKm() + 0.1);

                    // Simulated rating between 4.0 and 5.0
                    // In production: fetch from Driver Service
                    double simulatedRating = 4.0 + Math.random();

                    // Final weighted score
                    return (distanceScore * distanceWeight) + (simulatedRating * ratingWeight);
                }));
    }
}