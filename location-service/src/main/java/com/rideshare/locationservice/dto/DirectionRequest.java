package com.rideshare.locationservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for Google Maps Direction API.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectionRequest {

    private double originLatitude;
    private double originLongitude;
    private double destinationLatitude;
    private double destinationLongitude;

    /** Optional waypoints as intermediate lat/lng pairs */
    private List<Waypoint> waypoints;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Waypoint {
        private double latitude;
        private double longitude;
    }
}
