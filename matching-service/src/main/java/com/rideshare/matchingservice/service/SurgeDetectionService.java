package com.rideshare.matchingservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rideshare.matchingservice.dto.SurgeArea;

import lombok.extern.slf4j.Slf4j;

/**
 * Detects surge pricing areas based on driver-to-demand ratios.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
public class SurgeDetectionService {

    private static final int MIN_DRIVERS_FOR_NORMAL_DEMAND = 3;
    private static final double SURGE_THRESHOLD_RATIO = 2.5;

    /**
     * Detects surge areas based on driver-to-demand ratio.
     * In production, this would use real-time ride request counts.
     */
    public List<SurgeArea> detectSurgeAreas(double latitude, double longitude) {
        List<SurgeArea> surgeAreas = new ArrayList<>();

        // Simulated surge detection - in production, query ride-service for active requests
        // and location-service for driver counts per zone
        double simulatedDemand = 5 + (Math.random() * 20);
        int simulatedDrivers = (int) (2 + Math.random() * 8);

        if (simulatedDemand / Math.max(simulatedDrivers, 1) > SURGE_THRESHOLD_RATIO) {
            double multiplier = Math.min(3.0, 1.0 + (simulatedDemand / Math.max(simulatedDrivers, 1) - SURGE_THRESHOLD_RATIO) * 0.5);

            SurgeArea surgeArea = new SurgeArea();
            surgeArea.setZone("detected-zone");
            surgeArea.setCenterLatitude(latitude);
            surgeArea.setCenterLongitude(longitude);
            surgeArea.setRadiusKm(2.0);
            surgeArea.setDriverCount(simulatedDrivers);
            surgeArea.setActiveRideCount((int) simulatedDemand);
            surgeArea.setSurgeMultiplier(Math.round(multiplier * 10.0) / 10.0);

            surgeAreas.add(surgeArea);
            log.info("Surge detected at ({}, {}): multiplier={}, drivers={}, requests={}",
                    latitude, longitude, multiplier, simulatedDrivers, (int) simulatedDemand);
        }

        return surgeAreas;
    }

    /**
     * Gets the current surge multiplier for a specific location.
     */
    public double getSurgeMultiplier(double latitude, double longitude) {
        List<SurgeArea> surgeAreas = detectSurgeAreas(latitude, longitude);
        if (surgeAreas.isEmpty()) {
            return 1.0;
        }
        return surgeAreas.get(0).getSurgeMultiplier();
    }
}
