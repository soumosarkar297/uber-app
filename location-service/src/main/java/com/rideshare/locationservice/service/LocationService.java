package com.rideshare.locationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponse;
import com.rideshare.locationservice.handler.LocationWebSocketHandler;

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

    private static final String DRIVERS_GEO_KEY = "drivers:locations";
    private static final String DRIVERS_HASH_KEY = "drivers:metadata";

    /**
     * Updates the location of a driver in Redis. Stores GEO position and
     * metadata. Broadcasts the update via WebSocket for live tracking.
     */
    public void updateDriverLocation(DriverLocationRequest request) {
        log.info("Updating location for driver: {}", request.getDriverId());

        // Store in Redis GEO
        Point driverPoint = new Point(request.getLongitude(), request.getLatitude());
        redisTemplate.opsForGeo().add(DRIVERS_GEO_KEY, driverPoint, request.getDriverId());

        // Store metadata (heading, speed) in hash
        String metadata = String.format("%s,%s",
                request.getHeading() != null ? request.getHeading().toString() : "",
                request.getSpeed() != null ? request.getSpeed().toString() : "");
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
        log.info("Finding drivers near lat: {} long: {} within {}Km",
                latitude, longitude, radiusInKm);

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
                                .limit(10));

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
                        heading = Double.parseDouble(parts[0]);
                    }
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        speed = Double.parseDouble(parts[1]);
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
        double radiusKm = haversine(minLat, minLon, maxLat, maxLon) / 2;

        List<NearByDriverResponse> drivers = findNearByDrivers(centerLat, centerLon, radiusKm);

        // Filter to only include drivers within the rectangle
        return drivers.stream()
                .filter(d -> d.getLatitude() >= minLat && d.getLatitude() <= maxLat
                && d.getLongitude() >= minLon && d.getLongitude() <= maxLon)
                .toList();
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
