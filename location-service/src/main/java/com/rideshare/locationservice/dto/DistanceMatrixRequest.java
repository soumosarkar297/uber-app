package com.rideshare.locationservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for Google Maps Distance Matrix API.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for Google Maps Distance Matrix API")
public class DistanceMatrixRequest {

    @Schema(description = "List of origin location points")
    private List<LocationPoint> origins;

    @Schema(description = "List of destination location points")
    private List<LocationPoint> destinations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "A geographic location point defined by latitude and longitude")
    public static class LocationPoint {

        @Schema(description = "Latitude coordinate", example = "28.6139")
        private double latitude;

        @Schema(description = "Longitude coordinate", example = "77.2090")
        private double longitude;
    }
}
