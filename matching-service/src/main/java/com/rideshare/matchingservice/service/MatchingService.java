package com.rideshare.matchingservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.matchingservice.client.LocationServiceClient;
import com.rideshare.matchingservice.dto.NearByDriverResponse;
import com.rideshare.matchingservice.dto.SurgeArea;
import com.rideshare.matchingservice.event.RideMatchedEvent;
import com.rideshare.matchingservice.event.RideRequestedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Matches riders with the best available driver using multi-factor scoring.
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
    private final SurgeDetectionService surgeDetectionService;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;
    private static final double MAX_SEARCH_RADIUS_KM = 15.0;
    private static final double SEARCH_RADIUS_INCREMENT_KM = 2.5;

    // Scoring weights
    private static final double DISTANCE_WEIGHT = 0.40;
    private static final double RATING_WEIGHT = 0.25;
    private static final double ACCEPTANCE_RATE_WEIGHT = 0.20;
    private static final double TRIP_COUNT_WEIGHT = 0.15;

    /**
     * Main matching algorithm. Expands search radius if no drivers found.
     * Uses multi-factor scoring to select the best driver.
     */
    public void matchDriverForRide(RideRequestedEvent event) {
        double searchRadius = DEFAULT_SEARCH_RADIUS_KM;
        List<NearByDriverResponse> nearByDrivers = List.of();

        // Expanding radius search
        while (searchRadius <= MAX_SEARCH_RADIUS_KM && nearByDrivers.isEmpty()) {
            nearByDrivers = locationServiceClient.getNearByDrivers(
                    event.getPickupLatitude(),
                    event.getPickupLongitude(),
                    searchRadius);

            if (nearByDrivers.isEmpty()) {
                searchRadius += SEARCH_RADIUS_INCREMENT_KM;
                log.info("No drivers within {}km, expanding search...", searchRadius);
            }
        }

        if (nearByDrivers.isEmpty()) {
            log.warn("No drivers found for ride {} after expanding to {}km",
                    event.getRideId(), searchRadius);
            return;
        }

        // Filter: exclude drivers in surge zones that are too expensive
        List<SurgeArea> surgeAreas = surgeDetectionService.detectSurgeAreas(
                event.getPickupLatitude(), event.getPickupLongitude());

        // Score and select best driver
        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearByDrivers, surgeAreas);

        if (bestDriver.isEmpty()) {
            log.warn("Could not find suitable driver for ride: {}", event.getRideId());
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get();

        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm());

        kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), matchedEvent);
        log.info("RideMatchedEvent published for ride: {} to driver: {}",
                event.getRideId(), assignedDriver.getDriverId());
    }

    /**
     * Multi-factor driver scoring algorithm.
     * Score = distanceScore * 0.40 + ratingScore * 0.25 + acceptanceRate * 0.20 + tripCountScore * 0.15
     */
    private Optional<NearByDriverResponse> findBestDriver(
            List<NearByDriverResponse> drivers,
            List<SurgeArea> surgeAreas) {

        return drivers.stream()
                .max(Comparator.comparingDouble(driver -> calculateDriverScore(driver, surgeAreas)));
    }

    private double calculateDriverScore(NearByDriverResponse driver, List<SurgeArea> surgeAreas) {
        // Distance score: closer = higher score (normalized 0-1)
        double distanceScore = 1.0 / (driver.getDistanceInKm() + 0.1);

        // Rating score: normalized 0-1 (assuming 4.0-5.0 range)
        double simulatedRating = 4.0 + Math.random();
        double ratingScore = (simulatedRating - 3.0) / 2.0;

        // Acceptance rate: simulated 0.7-1.0
        double acceptanceRate = 0.7 + (Math.random() * 0.3);

        // Trip count: simulated normalized score
        double tripCountScore = Math.random();

        // Surge area penalty: reduce score if driver is in a surge area
        double surgePenalty = 1.0;
        for (SurgeArea area : surgeAreas) {
            double distToSurgeCenter = haversine(
                    driver.getLatitude(), driver.getLongitude(),
                    area.getCenterLatitude(), area.getCenterLongitude());
            if (distToSurgeCenter <= area.getRadiusKm()) {
                // Higher surge = higher penalty
                surgePenalty = Math.max(0.5, 1.0 - (area.getSurgeMultiplier() - 1.0) * 0.3);
                break;
            }
        }

        return (distanceScore * DISTANCE_WEIGHT
                + ratingScore * RATING_WEIGHT
                + acceptanceRate * ACCEPTANCE_RATE_WEIGHT
                + tripCountScore * TRIP_COUNT_WEIGHT)
                * surgePenalty;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371 * c;
    }
}
