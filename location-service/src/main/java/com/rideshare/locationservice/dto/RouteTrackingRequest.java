package com.rideshare.locationservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for recording route tracking points for an active ride")
public class RouteTrackingRequest {

    @Schema(description = "Unique identifier of the ride", example = "RIDE-67890")
    private String rideId;

    @Schema(description = "Unique identifier of the driver", example = "DRV-12345")
    private String driverId;

    @Schema(description = "List of GPS route points recorded during the ride")
    private List<RoutePoint> routePoints;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "A single GPS route point with timestamp and optional speed")
    public static class RoutePoint {

        @Schema(description = "Latitude coordinate of the route point", example = "28.6139")
        private double latitude;

        @Schema(description = "Longitude coordinate of the route point", example = "77.2090")
        private double longitude;

        @Schema(description = "Unix epoch timestamp in milliseconds when the point was recorded", example = "1751467800000")
        private long timestampEpoch;

        @Schema(description = "Speed in km/h at the time of recording (optional)", example = "45.5")
        private Double speed;
    }
}
