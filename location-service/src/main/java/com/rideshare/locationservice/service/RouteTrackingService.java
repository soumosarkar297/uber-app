package com.rideshare.locationservice.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.RouteTrackingRequest;
import com.rideshare.locationservice.dto.RouteTrackingResponse;
import com.rideshare.locationservice.util.GeoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages route tracking and summary generation for active rides.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RouteTrackingService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String ROUTE_TRACKING_PREFIX = "route:tracking:";

    /**
     * Records route points for an active ride.
     */
    public void recordRoute(String rideId, String driverId,
                            List<RouteTrackingRequest.RoutePoint> points) {
        String key = ROUTE_TRACKING_PREFIX + rideId;

        for (RouteTrackingRequest.RoutePoint point : points) {
            String value = String.format("%.6f,%.6f,%d,%s",
                    point.getLatitude(),
                    point.getLongitude(),
                    point.getTimestampEpoch(),
                    point.getSpeed() != null ? point.getSpeed().toString() : "");

            redisTemplate.opsForList().rightPush(key, value);
        }

        redisTemplate.expire(key, java.time.Duration.ofHours(24));
    }

    /**
     * Gets the route tracking summary for a ride.
     */
    public RouteTrackingResponse getRouteSummary(String rideId, String driverId) {
        String key = ROUTE_TRACKING_PREFIX + rideId;
        List<String> routePoints = redisTemplate.opsForList().range(key, 0, -1);

        if (routePoints == null || routePoints.isEmpty()) {
            return new RouteTrackingResponse(rideId, driverId, 0, 0, 0);
        }

        double totalDistance = 0;
        double totalSpeed = 0;
        int speedCount = 0;
        long firstTimestamp = 0;
        long lastTimestamp = 0;

        double prevLat = 0, prevLon = 0;

        for (int i = 0; i < routePoints.size(); i++) {
            String[] parts = routePoints.get(i).split(",");
            double lat = Double.parseDouble(parts[0]);
            double lon = Double.parseDouble(parts[1]);
            long timestamp = Long.parseLong(parts[2]);

            if (i == 0) {
                firstTimestamp = timestamp;
                prevLat = lat;
                prevLon = lon;
                continue;
            }

            totalDistance += GeoUtils.haversine(prevLat, prevLon, lat, lon);
            prevLat = lat;
            prevLon = lon;
            lastTimestamp = timestamp;

            if (parts.length > 3 && !parts[3].isEmpty()) {
                totalSpeed += Double.parseDouble(parts[3]);
                speedCount++;
            }
        }

        double hours = (lastTimestamp - firstTimestamp) / 3_600_000.0;
        double avgSpeed = hours > 0 ? totalDistance / hours : 0;
        if (speedCount > 0) {
            avgSpeed = totalSpeed / speedCount;
        }

        return new RouteTrackingResponse(
                rideId, driverId, totalDistance, avgSpeed, routePoints.size());
    }
}
