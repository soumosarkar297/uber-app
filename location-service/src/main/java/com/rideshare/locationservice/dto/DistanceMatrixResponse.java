package com.rideshare.locationservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from Google Maps Distance Matrix API.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response from Google Maps Distance Matrix API")
public class DistanceMatrixResponse {

    @Schema(description = "2D matrix of distance/duration elements, rows correspond to origins and columns to destinations")
    private List<List<Element>> rows;

    @Schema(description = "Overall API response status", example = "OK")
    private String status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "A single element in the distance matrix containing distance and duration between an origin and destination")
    public static class Element {

        @Schema(description = "Distance information for this origin-destination pair")
        private DistanceDistance distance;

        @Schema(description = "Duration information for this origin-destination pair")
        private DistanceDuration duration;

        @Schema(description = "Status of this individual element", example = "OK")
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Distance value with human-readable text and numeric value in meters")
    public static class DistanceDistance {

        @Schema(description = "Human-readable distance string", example = "15.3 km")
        private String text;

        @Schema(description = "Distance value in meters", example = "15300")
        private double value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Duration value with human-readable text and numeric value in seconds")
    public static class DistanceDuration {

        @Schema(description = "Human-readable duration string", example = "25 mins")
        private String text;

        @Schema(description = "Duration value in seconds", example = "1500")
        private double value;
    }
}
