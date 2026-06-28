package com.rideshare.locationservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing driver locations using Redis GeoSpatial indexing.
 * Provides operations to add, query, and remove driver locations. Uses Spring
 * Data Redis GEO commands for efficient spatial queries.
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

    // Redis key for all driver locations
    private static final String DRIVERS_GEO_KEY = "drivers:locations";

    /**
     * Updates the location of a driver in Redis. Called by the driver's app
     * approximately every 3 seconds. Maps to Redis GEOADD command.
     *
     * @param driverLocationRequest the request containing driver ID and
     * coordinates
     * @throws IllegalArgumentException if driverId is null/empty or coordinates
     * are invalid
     */
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
        log.info("Updating location for driver: {}", driverLocationRequest.getDriverId());

        // IMPORTANT: longitude FIRST, latitude SECOND - GeoSpatial Standard
        Point driverPoint = new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude());

        redisTemplate.opsForGeo().add(
                DRIVERS_GEO_KEY,
                driverPoint,
                driverLocationRequest.getDriverId());

        log.info("Location updated for driver: {}", driverLocationRequest.getDriverId());
    }

    /**
     * Finds nearby drivers within a given radius. Called by the Matching
     * Service when a ride is requested. Maps to Redis GEORADIUS command.
     *
     * @param latitude the latitude of the search center
     * @param longitude the longitude of the search center
     * @param radiusInKm the search radius in kilometers
     * @return list of nearby drivers with their locations and distances, sorted
     * by distance ascending (max 10)
     * @throws IllegalArgumentException if coordinates are invalid or radius is
     * negative
     */
    public List<NearByDriverResponse> findNearByDrivers(
            double latitude, double longitude, double radiusInKm
    ) {
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
                nearbyDrivers.add(
                        new NearByDriverResponse(
                                location.getName(),
                                location.getPoint().getY(),
                                location.getPoint().getX(),
                                result.getDistance().getValue()));
            });
        }

        log.info("Found {} drivers nearby", nearbyDrivers.size());
        return nearbyDrivers;
    }

    /**
     * Removes a driver from the location tracking system. Called when a driver
     * goes offline. Maps to Redis ZREM command.
     *
     * @param driverId the unique identifier of the driver to remove
     * @throws IllegalArgumentException if driverId is null or empty
     */
    public void removeDriver(String driverId) {
        log.info("Removing driver: {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, driverId);
    }

}
