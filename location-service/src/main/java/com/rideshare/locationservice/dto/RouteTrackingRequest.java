package com.rideshare.locationservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for recording route tracking points for an active ride.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteTrackingRequest {

    private String rideId;

    private String driverId;

    private List<RoutePoint> routePoints;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoutePoint {
        private double latitude;
        private double longitude;
        private long timestampEpoch;
        private Double speed;
    }
}
