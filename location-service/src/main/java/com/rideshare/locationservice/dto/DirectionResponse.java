package com.rideshare.locationservice.dto;

import java.util.List;

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
public class DirectionResponse {

    private double distanceKm;
    private double durationMinutes;
    private String polyline;
    private List<Step> steps;
    private String status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Step {
        private String instruction;
        private double distanceKm;
        private double durationMinutes;
        private String polyline;
        private double startLatitude;
        private double startLongitude;
        private double endLatitude;
        private double endLongitude;
    }
}
