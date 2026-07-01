package com.rideshare.pricingservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.pricingservice.dto.SurgePricingRequest;
import com.rideshare.pricingservice.dto.SurgePricingResponse;
import com.rideshare.pricingservice.entity.SurgeHistory;
import com.rideshare.pricingservice.repository.SurgeHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Calculates surge multipliers based on demand-supply ratios with Redis caching.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SurgePricingService {

    private final SurgeHistoryRepository surgeHistoryRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SURGE_CACHE_PREFIX = "pricing:surge:";
    private static final int MIN_DRIVERS_FOR_NORMAL = 3;
    private static final double SURGE_THRESHOLD = 2.5;

    /**
     * Calculates surge multiplier based on driver-to-ride ratio.
     * Uses Redis for real-time caching.
     */
    public SurgePricingResponse calculateSurge(SurgePricingRequest request) {
        String cacheKey = SURGE_CACHE_PREFIX + request.getZone();

        // Check cached surge
        String cached = redisTemplate.opsForValue().get(cacheKey);
        double cachedMultiplier = cached != null ? Double.parseDouble(cached) : 1.0;

        // Simulate driver/ride counts (in production, query location-service and ride-service)
        int driverCount = (int) (3 + Math.random() * 10);
        int activeRideCount = (int) (2 + Math.random() * 15);

        double demandSupplyRatio = (double) activeRideCount / Math.max(driverCount, 1);
        double surgeMultiplier = 1.0;

        if (demandSupplyRatio > SURGE_THRESHOLD) {
            surgeMultiplier = Math.min(3.0,
                    1.0 + (demandSupplyRatio - SURGE_THRESHOLD) * 0.5);
            surgeMultiplier = Math.round(surgeMultiplier * 10.0) / 10.0;
        }

        // Cache surge for 5 minutes
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(surgeMultiplier),
                java.time.Duration.ofMinutes(5));

        // Save to history
        SurgeHistory history = new SurgeHistory();
        history.setZone(request.getZone());
        history.setSurgeMultiplier(BigDecimal.valueOf(surgeMultiplier));
        history.setDriverCount(driverCount);
        history.setActiveRideCount(activeRideCount);
        history.setRecordedAt(java.time.LocalDateTime.now());
        surgeHistoryRepository.save(history);

        String status = surgeMultiplier > 1.0 ? "SURGE_ACTIVE" : "NORMAL";

        return new SurgePricingResponse(
                request.getZone(),
                surgeMultiplier,
                driverCount,
                activeRideCount,
                BigDecimal.ZERO,
                status);
    }

    /**
     * Gets the current surge multiplier for a zone from cache.
     */
    public double getCurrentSurgeMultiplier(String zone) {
        String cacheKey = SURGE_CACHE_PREFIX + zone;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        return cached != null ? Double.parseDouble(cached) : 1.0;
    }

    /**
     * Checks if surge pricing is active for a given area.
     */
    public boolean isSurgeActive(String zone) {
        return getCurrentSurgeMultiplier(zone) > 1.0;
    }
}
