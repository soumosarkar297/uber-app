package com.rideshare.locationservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponse;
import com.rideshare.locationservice.event.LocationEventPublisher;
import com.rideshare.locationservice.event.LocationUpdatedEvent;
import com.rideshare.locationservice.handler.LocationWebSocketHandler;
import com.rideshare.locationservice.util.GeoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages real-time driver location tracking and nearby driver search using
 * Redis.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final LocationWebSocketHandler webSocketHandler;
    private final LocationHistoryService locationHistoryService;
    private final LocationEventPublisher locationEventPublisher;
    private final DriverAvailabilityService driverAvailabilityService;

    private static final String DRIVERS_GEO_KEY = "drivers:locations";
    private static final String DRIVERS_HASH_KEY = "drivers:metadata";
    private static final int DEFAULT_NEARBY_LIMIT = 10;
    private static final int MAX_NEARBY_LIMIT = 50;

    /**
     * Updates the location of a driver in Redis. Stores GEO position and
     * metadata. Broadcasts the update via WebSocket for live tracking.
     */
    public void updateDriverLocation(DriverLocationRequest request) {
        log.info("Updating location for driver: {}", request.getDriverId());

        // Store in Redis GEO
        Point driverPoint = new Point(request.getLongitude(), request.getLatitude());
        redisTemplate.opsForGeo().add(DRIVERS_GEO_KEY, driverPoint, request.getDriverId());

        // Store metadata (heading, speed, timestamp) in hash
        String metadata = String.format("%s,%s,%d",
                request.getHeading() != null ? request.getHeading().toString() : "",
                request.getSpeed() != null ? request.getSpeed().toString() : "",
                System.currentTimeMillis());
        redisTemplate.opsForHash().put(DRIVERS_HASH_KEY, request.getDriverId(), metadata);

        // Record in history
        locationHistoryService.recordLocation(
                request.getDriverId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getHeading(),
                request.getSpeed());

        // Broadcast via WebSocket
        webSocketHandler.broadcastDriverLocation(request);

        // Publish location updated event to Kafka
        String zone = driverAvailabilityService.getDriverZone(request.getDriverId());
        locationEventPublisher.publishLocationUpdated(new LocationUpdatedEvent(
                request.getDriverId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getHeading(),
                request.getSpeed(),
                zone,
                LocalDateTime.now()));

        log.info("Location updated for driver: {}", request.getDriverId());
    }

    /**
     * Updates location and broadcasts to ride-specific topic for live trip
     * tracking.
     */
    public void updateDriverLocationForRide(String rideId, DriverLocationRequest request) {
        updateDriverLocation(request);
        webSocketHandler.broadcastRideLocation(rideId, request);
    }

    /**
     * Finds nearby drivers within a given radius with metadata.
     */
    public List<NearByDriverResponse> findNearByDrivers(
            double latitude, double longitude, double radiusInKm) {
        return findNearByDrivers(latitude, longitude, radiusInKm, DEFAULT_NEARBY_LIMIT);
    }

    public List<NearByDriverResponse> findNearByDrivers(
            double latitude, double longitude, double radiusInKm, int limit) {
        log.info("Finding drivers near lat: {} long: {} within {}Km (limit: {})",
                latitude, longitude, radiusInKm, limit);

        int effectiveLimit = Math.min(Math.max(limit, 1), MAX_NEARBY_LIMIT);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate
                .opsForGeo()
                .radius(
                        DRIVERS_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(effectiveLimit));

        List<NearByDriverResponse> nearbyDrivers = new ArrayList<>();

        if (results != null) {
            results.getContent().forEach(result -> {
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                String driverId = location.getName();

                // Get metadata
                String metadata = (String) redisTemplate.opsForHash()
                        .get(DRIVERS_HASH_KEY, driverId);
                Double heading = null;
                Double speed = null;
                if (metadata != null && !metadata.isEmpty()) {
                    String[] parts = metadata.split(",");
                    if (parts.length > 0 && !parts[0].isEmpty()) {
                        heading = Double.valueOf(parts[0]);
                    }
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        speed = Double.valueOf(parts[1]);
                    }
                }

                nearbyDrivers.add(new NearByDriverResponse(
                        driverId,
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getDistance().getValue(),
                        heading,
                        speed));
            });
        }

        log.info("Found {} drivers nearby", nearbyDrivers.size());
        return nearbyDrivers;
    }

    /**
     * Finds nearby available drivers only.
     */
    public List<NearByDriverResponse> findNearbyAvailableDrivers(
            double latitude, double longitude, double radiusInKm) {
        List<NearByDriverResponse> allNearby = findNearByDrivers(latitude, longitude, radiusInKm);
        return allNearby.stream()
                .filter(driver -> {
                    Boolean isAvailable = redisTemplate.opsForSet()
                            .isMember("drivers:available", driver.getDriverId());
                    return Boolean.TRUE.equals(isAvailable);
                })
                .toList();
    }

    /**
     * Removes a driver from the location tracking system.
     */
    public void removeDriver(String driverId) {
        log.info("Removing driver: {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, driverId);
        redisTemplate.opsForHash().delete(DRIVERS_HASH_KEY, driverId);
    }

    /**
     * Gets a driver's current location.
     */
    public Point getDriverLocation(String driverId) {
        java.util.List<Point> points = redisTemplate.opsForGeo().position(DRIVERS_GEO_KEY, driverId);
        if (points != null && !points.isEmpty() && points.get(0) != null) {
            Point point = points.get(0);
            return new Point(point.getX(), point.getY());
        }
        return null;
    }

    /**
     * Gets the distance between two drivers in km.
     */
    public Double getDistanceBetweenDrivers(String driverId1, String driverId2) {
        Distance distance = redisTemplate.opsForGeo().distance(
                DRIVERS_GEO_KEY, driverId1, driverId2, Metrics.KILOMETERS);
        return distance != null ? distance.getValue() : null;
    }

    /**
     * Gets the number of drivers in a given area.
     */
    public long getDriverCountInArea(double latitude, double longitude, double radiusInKm) {
        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate
                .opsForGeo()
                .radius(DRIVERS_GEO_KEY, searchArea);

        return results != null ? results.getContent().size() : 0;
    }

    /**
     * Searches for drivers within a rectangular area.
     */
    public List<NearByDriverResponse> findDriversInRectangularArea(
            double minLat, double maxLat, double minLon, double maxLon) {
        // Approximate center of the rectangular area
        double centerLat = (minLat + maxLat) / 2;
        double centerLon = (minLon + maxLon) / 2;

        // Calculate approximate radius to cover the rectangle
        double radiusKm = GeoUtils.haversine(minLat, minLon, maxLat, maxLon) / 2;

        List<NearByDriverResponse> drivers = findNearByDrivers(centerLat, centerLon, radiusKm);

        // Filter to only include drivers within the rectangle
        return drivers.stream()
                .filter(d -> d.getLatitude() >= minLat && d.getLatitude() <= maxLat
                && d.getLongitude() >= minLon && d.getLongitude() <= maxLon)
                .toList();
    }

    /**
     * Removes drivers whose last location update exceeds the stale threshold.
     * Runs every 60 seconds to prevent ghost drivers in the GEO index.
     */
    @Scheduled(fixedRate = 60_000)
    public void cleanupStaleDrivers() {
        long cutoff = System.currentTimeMillis() - 300_000; // 5 minutes
        Map<Object, Object> allMetadata = redisTemplate.opsForHash().entries(DRIVERS_HASH_KEY);
        int removed = 0;

        for (Map.Entry<Object, Object> entry : allMetadata.entrySet()) {
            String driverId = (String) entry.getKey();
            String meta = (String) entry.getValue();

            if (meta == null || meta.isEmpty()) {
                removeDriver(driverId);
                removed++;
                continue;
            }

            String[] parts = meta.split(",");
            if (parts.length >= 3) {
                try {
                    long lastUpdate = Long.parseLong(parts[2].trim());
                    if (lastUpdate < cutoff) {
                        removeDriver(driverId);
                        removed++;
                    }
                } catch (NumberFormatException e) {
                    removeDriver(driverId);
                    removed++;
                }
            }
        }

        if (removed > 0) {
            log.info("Cleaned up {} stale driver locations", removed);
        }
    }

}
