package com.rideshare.locationservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from Google Maps Direction API with route details.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response from Google Maps Direction API with route details")
public class DirectionResponse {

    @Schema(description = "Total route distance in kilometers", example = "15.3")
    private double distanceKm;

    @Schema(description = "Total route duration in minutes", example = "25.0")
    private double durationMinutes;

    @Schema(description = "Encoded polyline representing the route path")
    private String polyline;

    @Schema(description = "List of route steps with turn-by-turn directions")
    private List<Step> steps;

    @Schema(description = "API response status", example = "OK")
    private String status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "A single step within a route direction")
    public static class Step {

        @Schema(description = "Human-readable driving instruction for this step", example = "Turn right onto Main St")
        private String instruction;

        @Schema(description = "Distance of this step in kilometers", example = "1.2")
        private double distanceKm;

        @Schema(description = "Duration of this step in minutes", example = "3.5")
        private double durationMinutes;

        @Schema(description = "Encoded polyline for this step")
        private String polyline;

        @Schema(description = "Latitude coordinate where this step starts", example = "28.6139")
        private double startLatitude;

        @Schema(description = "Longitude coordinate where this step starts", example = "77.2090")
        private double startLongitude;

        @Schema(description = "Latitude coordinate where this step ends", example = "28.6200")
        private double endLatitude;

        @Schema(description = "Longitude coordinate where this step ends", example = "77.2150")
        private double endLongitude;
    }
}
