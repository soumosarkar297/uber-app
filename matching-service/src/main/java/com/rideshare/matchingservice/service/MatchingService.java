package com.rideshare.matchingservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.matchingservice.client.DriverServiceClient;
import com.rideshare.matchingservice.client.LocationServiceClient;
import com.rideshare.matchingservice.dto.DriverMetricsResponse;
import com.rideshare.matchingservice.dto.NearByDriverResponse;
import com.rideshare.matchingservice.dto.SurgeArea;
import com.rideshare.matchingservice.event.RideDeclinedEvent;
import com.rideshare.matchingservice.event.RideMatchedEvent;
import com.rideshare.matchingservice.event.RideRequestedEvent;
import com.rideshare.matchingservice.util.GeoUtils;

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
    private final DriverServiceClient driverServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;
    private final KafkaTemplate<String, RideDeclinedEvent> declinedKafkaTemplate;
    private final SurgeDetectionService surgeDetectionService;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final String RIDE_DECLINED_TOPIC = "ride.declined";
    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;
    private static final double MAX_SEARCH_RADIUS_KM = 15.0;
    private static final double SEARCH_RADIUS_INCREMENT_KM = 2.5;

    // Scoring weights
    private static final double DISTANCE_WEIGHT = 0.40;
    private static final double RATING_WEIGHT = 0.25;
    private static final double ACCEPTANCE_RATE_WEIGHT = 0.20;
    private static final double TRIP_COUNT_WEIGHT = 0.15;

    // Default fallback metrics when driver service is unavailable
    private static final double DEFAULT_RATING = 4.5;
    private static final int DEFAULT_TRIPS = 10;
    private static final double DEFAULT_ACCEPTANCE_RATE = 0.85;

    /**
     * Main matching algorithm. Expands search radius if no drivers found.
     * Uses multi-factor scoring with real driver metrics from driver-service.
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

            declinedKafkaTemplate.send(RIDE_DECLINED_TOPIC, event.getRideId(),
                    new RideDeclinedEvent(
                            event.getRideId(),
                            "NO_DRIVERS_AVAILABLE",
                            String.valueOf(searchRadius)
                    ));
            return;
        }

        // Filter: exclude drivers in surge zones that are too expensive
        List<SurgeArea> surgeAreas = surgeDetectionService.detectSurgeAreas(
                event.getPickupLatitude(), event.getPickupLongitude());

        // Fetch real driver metrics for all candidates
        List<EnrichedDriver> enrichedDrivers = nearByDrivers.stream()
                .map(driver -> new EnrichedDriver(driver, fetchDriverMetrics(driver.getDriverId())))
                .toList();

        // Score and select best driver
        Optional<EnrichedDriver> bestDriver = findBestDriver(enrichedDrivers, surgeAreas);

        if (bestDriver.isEmpty()) {
            log.warn("Could not find suitable driver for ride: {}", event.getRideId());

            declinedKafkaTemplate.send(RIDE_DECLINED_TOPIC, event.getRideId(),
                    new RideDeclinedEvent(
                            event.getRideId(),
                            "NO_SUITABLE_DRIVER",
                            null
                    ));
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get().nearby();

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
     * Fetches real driver metrics from driver-service via Feign.
     * Falls back to defaults if the service is unavailable.
     */
    private DriverMetricsResponse fetchDriverMetrics(String driverId) {
        try {
            return driverServiceClient.getDriverById(UUID.fromString(driverId));
        } catch (Exception e) {
            log.warn("Failed to fetch metrics for driver {}: {}", driverId, e.getMessage());
            return new DriverMetricsResponse(null, DEFAULT_RATING, DEFAULT_TRIPS, true, true);
        }
    }

    /**
     * Multi-factor driver scoring algorithm using real metrics.
     * Score = distanceScore * 0.40 + ratingScore * 0.25 + acceptanceRate * 0.20 + tripCountScore * 0.15
     */
    private Optional<EnrichedDriver> findBestDriver(
            List<EnrichedDriver> drivers,
            List<SurgeArea> surgeAreas) {

        return drivers.stream()
                .max(Comparator.comparingDouble(driver -> calculateDriverScore(driver, surgeAreas)));
    }

    private double calculateDriverScore(EnrichedDriver enriched, List<SurgeArea> surgeAreas) {
        NearByDriverResponse driver = enriched.nearby();
        DriverMetricsResponse metrics = enriched.metrics();

        // Distance score: closer = higher score (normalized 0-1)
        double distanceScore = 1.0 / (driver.getDistanceInKm() + 0.1);

        // Rating score: normalized 0-1 (scale: 1.0-5.0 -> 0.0-1.0)
        double rating = metrics.getRating() != null ? metrics.getRating() : DEFAULT_RATING;
        double ratingScore = (rating - 1.0) / 4.0;

        // Acceptance rate: use default if not available from driver service
        double acceptanceRate = DEFAULT_ACCEPTANCE_RATE;

        // Trip count score: normalized 0-1 (cap at 1000 trips)
        int totalTrips = metrics.getTotalTrips() != null ? metrics.getTotalTrips() : DEFAULT_TRIPS;
        double tripCountScore = Math.min(totalTrips / 1000.0, 1.0);

        // Surge area penalty: reduce score if driver is in a surge area
        double surgePenalty = 1.0;
        for (SurgeArea area : surgeAreas) {
            double distToSurgeCenter = GeoUtils.haversine(
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

    /**
     * Internal record pairing a nearby driver with their fetched metrics.
     */
    private record EnrichedDriver(NearByDriverResponse nearby, DriverMetricsResponse metrics) {}
}
