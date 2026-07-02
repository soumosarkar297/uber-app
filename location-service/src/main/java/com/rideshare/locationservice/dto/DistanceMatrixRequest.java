package com.rideshare.locationservice.dto;

import java.util.List;

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
public class DistanceMatrixRequest {

    private List<LocationPoint> origins;
    private List<LocationPoint> destinations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationPoint {
        private double latitude;
        private double longitude;
    }
}
