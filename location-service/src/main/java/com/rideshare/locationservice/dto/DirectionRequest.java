package com.rideshare.locationservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for Google Maps Direction API")
public class DirectionRequest {

    @Schema(description = "Latitude coordinate of the origin point", example = "28.6139")
    private double originLatitude;

    @Schema(description = "Longitude coordinate of the origin point", example = "77.2090")
    private double originLongitude;

    @Schema(description = "Latitude coordinate of the destination point", example = "28.7041")
    private double destinationLatitude;

    @Schema(description = "Longitude coordinate of the destination point", example = "77.1025")
    private double destinationLongitude;

    @Schema(description = "Optional list of waypoints as intermediate lat/lng pairs")
    private List<Waypoint> waypoints;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "An intermediate waypoint with latitude and longitude")
    public static class Waypoint {

        @Schema(description = "Latitude coordinate of the waypoint", example = "28.6500")
        private double latitude;

        @Schema(description = "Longitude coordinate of the waypoint", example = "77.2300")
        private double longitude;
    }
}
