package com.rideshare.tripservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Real-time analytics aggregation service using Redis for counter and metric storage.
 * Aggregates ride events, driver performance metrics, and revenue data.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsAggregationService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RIDE_COUNT_PREFIX = "analytics:rides:count:";
    private static final String REVENUE_PREFIX = "analytics:revenue:";
    private static final String DRIVER_TRIPS_PREFIX = "analytics:driver:trips:";
    private static final String DRIVER_EARNINGS_PREFIX = "analytics:driver:earnings:";
    private static final String ZONE_DEMAND_PREFIX = "analytics:zone:demand:";
    private static final String HOURLY_REVENUE_PREFIX = "analytics:hourly:revenue:";

    private static final long CENTS_MULTIPLIER = 100;
    private static final Duration ANALYTICS_TTL = Duration.ofHours(48);
    private static final Duration DRIVER_METRICS_TTL = Duration.ofDays(30);

    /**
     * Records a completed ride event for analytics aggregation.
     * All monetary values are stored as cents (long) for atomic increment operations.
     */
    public void recordRideCompleted(String rideId, String driverId, String riderId,
                                     BigDecimal fare, double distanceKm,
                                     double durationMinutes, String zone) {
        String today = LocalDateTime.now().toLocalDate().toString();

        // Increment ride count
        redisTemplate.opsForValue().increment(RIDE_COUNT_PREFIX + "total:" + today);
        redisTemplate.opsForValue().increment(RIDE_COUNT_PREFIX + "completed:" + today);

        // Atomic revenue update (stored in cents)
        long fareInCents = fare.multiply(BigDecimal.valueOf(CENTS_MULTIPLIER)).longValue();
        String revenueKey = REVENUE_PREFIX + today;
        redisTemplate.opsForValue().increment(revenueKey, fareInCents);
        redisTemplate.expire(revenueKey, ANALYTICS_TTL);

        // Atomic driver metrics update
        redisTemplate.opsForValue().increment(DRIVER_TRIPS_PREFIX + driverId);
        redisTemplate.expire(DRIVER_TRIPS_PREFIX + driverId, DRIVER_METRICS_TTL);
        String driverEarningsKey = DRIVER_EARNINGS_PREFIX + driverId;
        redisTemplate.opsForValue().increment(driverEarningsKey, fareInCents);
        redisTemplate.expire(driverEarningsKey, DRIVER_METRICS_TTL);

        // Update zone demand
        if (zone != null) {
            redisTemplate.opsForValue().increment(ZONE_DEMAND_PREFIX + zone + ":" + today);
        }

        // Update hourly revenue
        String hour = LocalDateTime.now().getHour() + ":00";
        redisTemplate.opsForValue().increment(HOURLY_REVENUE_PREFIX + today + ":" + hour);

        log.debug("Analytics recorded for ride: {} driver: {} fare: {}", rideId, driverId, fare);
    }

    /**
     * Records a cancelled ride event.
     */
    public void recordRideCancelled(String rideId, String zone) {
        String today = LocalDateTime.now().toLocalDate().toString();
        redisTemplate.opsForValue().increment(RIDE_COUNT_PREFIX + "cancelled:" + today);

        if (zone != null) {
            redisTemplate.opsForValue().increment(ZONE_DEMAND_PREFIX + zone + ":" + today);
        }
    }

    /**
     * Records a location update for demand heat map aggregation.
     */
    public void recordLocationUpdate(String driverId, String zone) {
        if (zone != null) {
            String today = LocalDateTime.now().toLocalDate().toString();
            String hour = LocalDateTime.now().getHour() + ":00";
            redisTemplate.opsForValue().increment(
                    "analytics:location:updates:" + zone + ":" + today + ":" + hour);
        }
    }

    /**
     * Gets the total rides completed today.
     */
    public long getTodayCompletedRides() {
        String today = LocalDateTime.now().toLocalDate().toString();
        String count = redisTemplate.opsForValue().get(RIDE_COUNT_PREFIX + "completed:" + today);
        return count != null ? Long.parseLong(count) : 0;
    }

    /**
     * Gets the total revenue today (converted from cents).
     */
    public BigDecimal getTodayRevenue() {
        String today = LocalDateTime.now().toLocalDate().toString();
        String revenue = redisTemplate.opsForValue().get(REVENUE_PREFIX + today);
        if (revenue != null) {
            return new BigDecimal(revenue)
                    .divide(BigDecimal.valueOf(CENTS_MULTIPLIER), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets a driver's total trips.
     */
    public long getDriverTotalTrips(String driverId) {
        String count = redisTemplate.opsForValue().get(DRIVER_TRIPS_PREFIX + driverId);
        return count != null ? Long.parseLong(count) : 0;
    }

    /**
     * Gets a driver's total earnings (converted from cents).
     */
    public BigDecimal getDriverTotalEarnings(String driverId) {
        String earnings = redisTemplate.opsForValue().get(DRIVER_EARNINGS_PREFIX + driverId);
        if (earnings != null) {
            return new BigDecimal(earnings)
                    .divide(BigDecimal.valueOf(CENTS_MULTIPLIER), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets demand count for a specific zone today.
     */
    public long getZoneDemand(String zone) {
        String today = LocalDateTime.now().toLocalDate().toString();
        String demand = redisTemplate.opsForValue().get(ZONE_DEMAND_PREFIX + zone + ":" + today);
        return demand != null ? Long.parseLong(demand) : 0;
    }

    /**
     * Gets hourly revenue breakdown for today.
     */
    public List<String> getHourlyRevenueBreakdown() {
        String today = LocalDateTime.now().toLocalDate().toString();
        List<String> breakdown = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            String key = HOURLY_REVENUE_PREFIX + today + ":" + hour + ":00";
            String revenue = redisTemplate.opsForValue().get(key);
            breakdown.add(revenue != null ? revenue : "0");
        }
        return breakdown;
    }

    /**
     * Gets real-time dashboard metrics.
     */
    public DashboardMetrics getDashboardMetrics() {
        return new DashboardMetrics(
                getTodayCompletedRides(),
                getTodayRevenue(),
                getHourlyRevenueBreakdown());
    }

    public record DashboardMetrics(
            long totalRides,
            BigDecimal totalRevenue,
            List<String> hourlyRevenue
    ) {}
}
