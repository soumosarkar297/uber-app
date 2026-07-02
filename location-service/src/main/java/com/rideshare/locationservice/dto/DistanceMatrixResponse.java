package com.rideshare.locationservice.dto;

import java.util.List;

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
public class DistanceMatrixResponse {

    private List<List<Element>> rows;
    private String status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Element {
        private DistanceDistance distance;
        private DistanceDuration duration;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DistanceDistance {
        private String text;
        private double value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DistanceDuration {
        private String text;
        private double value;
    }
}
