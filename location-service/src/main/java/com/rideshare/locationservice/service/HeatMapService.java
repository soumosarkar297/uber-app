package com.rideshare.locationservice.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.SurgeAreaResponse;
import com.rideshare.locationservice.util.GeoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Aggregates ride request data for demand heat map visualization.
 * Uses Redis to store and query demand data by geographic grid cells.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HeatMapService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String DEMAND_PREFIX = "heatmap:demand:";
    private static final String DRIVER_SUPPLY_PREFIX = "heatmap:supply:";
    private static final double CELL_SIZE_DEGREES = 0.01;
    private static final long DEMAND_TTL_HOURS = 24;

    /**
     * Records a ride request for heat map aggregation.
     * Quantizes the location into a grid cell and increments the demand counter.
     */
    public void recordRideRequest(double latitude, double longitude, String zone) {
        String cellKey = getCellKey(latitude, longitude);
        String today = LocalDateTime.now().toLocalDate().toString();
        String key = DEMAND_PREFIX + cellKey + ":" + today;

        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofHours(DEMAND_TTL_HOURS));

        if (zone != null) {
            redisTemplate.opsForValue().increment(DEMAND_PREFIX + "zone:" + zone + ":" + today);
            redisTemplate.expire(DEMAND_PREFIX + "zone:" + zone + ":" + today,
                    Duration.ofHours(DEMAND_TTL_HOURS));
        }

        log.debug("Demand recorded at ({}, {}) cell: {}", latitude, longitude, cellKey);
    }

    /**
     * Records driver supply (available drivers) for heat map.
     */
    public void recordDriverSupply(double latitude, double longitude) {
        String cellKey = getCellKey(latitude, longitude);
        String today = LocalDateTime.now().toLocalDate().toString();
        String key = DRIVER_SUPPLY_PREFIX + cellKey + ":" + today;

        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofHours(DEMAND_TTL_HOURS));
    }

    /**
     * Gets demand count for a specific grid cell today.
     */
    public long getCellDemand(double latitude, double longitude) {
        String cellKey = getCellKey(latitude, longitude);
        String today = LocalDateTime.now().toLocalDate().toString();
        String key = DEMAND_PREFIX + cellKey + ":" + today;

        String count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(count) : 0;
    }

    /**
     * Gets demand count for a specific zone today.
     */
    public long getZoneDemand(String zone) {
        String today = LocalDateTime.now().toLocalDate().toString();
        String key = DEMAND_PREFIX + "zone:" + zone + ":" + today;

        String count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(count) : 0;
    }

    /**
     * Calculates demand-supply ratio for a specific area.
     * Returns the surge multiplier based on the ratio.
     */
    public double getDemandSupplyRatio(double latitude, double longitude) {
        String cellKey = getCellKey(latitude, longitude);
        String today = LocalDateTime.now().toLocalDate().toString();

        String demandStr = redisTemplate.opsForValue().get(DEMAND_PREFIX + cellKey + ":" + today);
        String supplyStr = redisTemplate.opsForValue().get(DRIVER_SUPPLY_PREFIX + cellKey + ":" + today);

        long demand = demandStr != null ? Long.parseLong(demandStr) : 0;
        long supply = supplyStr != null ? Long.parseLong(supplyStr) : 1;

        return (double) demand / Math.max(supply, 1);
    }

    /**
     * Identifies surge areas based on demand-supply ratio.
     * Returns a list of areas where demand exceeds supply.
     */
    public List<SurgeAreaResponse> identifySurgeAreas(double centerLat, double centerLon,
                                                       double radiusKm) {
        List<SurgeAreaResponse> surgeAreas = new ArrayList<>();

        double deltaDegrees = radiusKm / 111.0;
        for (double lat = centerLat - deltaDegrees; lat <= centerLat + deltaDegrees; lat += CELL_SIZE_DEGREES) {
            for (double lon = centerLon - deltaDegrees; lon <= centerLon + deltaDegrees; lon += CELL_SIZE_DEGREES) {
                String cellKey = getCellKey(lat, lon);
                String today = LocalDateTime.now().toLocalDate().toString();

                long demand = getCellDemandCount(cellKey, today);
                long supply = getCellSupplyCount(cellKey, today);
                long ratio = demand > 0 ? demand / Math.max(supply, 1) : 0;

                if (ratio > 2) {
                    double multiplier = Math.min(3.0, 1.0 + (ratio - 2.0) * 0.3);
                    surgeAreas.add(new SurgeAreaResponse(
                            "cell-" + cellKey,
                            lat, lon,
                            CELL_SIZE_DEGREES * 111.0,
                            (int) supply,
                            (int) demand,
                            Math.round(multiplier * 10.0) / 10.0));
                }
            }
        }

        return surgeAreas;
    }

    private long getCellDemandCount(String cellKey, String today) {
        String demandStr = redisTemplate.opsForValue().get(DEMAND_PREFIX + cellKey + ":" + today);
        return demandStr != null ? Long.parseLong(demandStr) : 0;
    }

    private long getCellSupplyCount(String cellKey, String today) {
        String supplyStr = redisTemplate.opsForValue().get(DRIVER_SUPPLY_PREFIX + cellKey + ":" + today);
        return supplyStr != null ? Long.parseLong(supplyStr) : 0;
    }

    /**
     * Gets all demand data points for heat map visualization.
     * Returns a list of cells with their demand counts.
     */
    public List<HeatMapPoint> getHeatMapData(double centerLat, double centerLon,
                                              double radiusKm) {
        List<HeatMapPoint> points = new ArrayList<>();
        double deltaDegrees = radiusKm / 111.0;
        String today = LocalDateTime.now().toLocalDate().toString();

        for (double lat = centerLat - deltaDegrees; lat <= centerLat + deltaDegrees; lat += CELL_SIZE_DEGREES) {
            for (double lon = centerLon - deltaDegrees; lon <= centerLon + deltaDegrees; lon += CELL_SIZE_DEGREES) {
                String cellKey = getCellKey(lat, lon);
                String demandKey = DEMAND_PREFIX + cellKey + ":" + today;
                String supplyKey = DRIVER_SUPPLY_PREFIX + cellKey + ":" + today;

                String demandStr = redisTemplate.opsForValue().get(demandKey);
                String supplyStr = redisTemplate.opsForValue().get(supplyKey);

                long demand = demandStr != null ? Long.parseLong(demandStr) : 0;
                long supply = supplyStr != null ? Long.parseLong(supplyStr) : 0;

                if (demand > 0 || supply > 0) {
                    points.add(new HeatMapPoint(
                            GeoUtils.round(lat, 4),
                            GeoUtils.round(lon, 4),
                            demand, supply,
                            demand > 0 ? GeoUtils.round((double) demand / Math.max(supply, 1), 2) : 0));
                }
            }
        }

        return points;
    }

    private String getCellKey(double latitude, double longitude) {
        int latCell = (int) (latitude / CELL_SIZE_DEGREES);
        int lonCell = (int) (longitude / CELL_SIZE_DEGREES);
        return latCell + ":" + lonCell;
    }

    public record HeatMapPoint(
            double latitude,
            double longitude,
            long demandCount,
            long driverSupply,
            double demandSupplyRatio
    ) {}
}
