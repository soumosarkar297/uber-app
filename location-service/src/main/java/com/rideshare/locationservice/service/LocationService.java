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

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis key for all driver locations
    private static final String DRIVERS_GEO_KEY = "drivers:locations";

    /**
     * Update driver location in Redis. Called every 3 seconds by driver's app.
     * Maps to Redis GEOADD command.
     *
     * @param driverLocationRequest
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
     * Find nearby drivers within given radius. Called by Matching Service on
     * ride request. Maps to Redis GEORADIUS command.
     *
     * @param latitude
     * @param longitute
     * @param radiusInKm
     * @return List <NearByDriverResponse>
     */
    public List<NearByDriverResponse> findNearByDrivers(
            double latitude, double longitute, double radiusInKm
    ) {
        log.info("Finding drivers near lat: {} long: {} within {}Km",
                latitude, longitute, radiusInKm);

        Circle searchArea = new Circle(
                new Point(longitute, latitude),
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
     * Remove driver when they go offline. Maps to Redis ZREM command.
     *
     * @param driverId
     */
    public void removeDriver(String driverId) {
        log.info("Removing driver: {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, driverId);
    }

}
