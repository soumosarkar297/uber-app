package com.rideshare.pricingservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.pricingservice.dto.BaseFareRequest;
import com.rideshare.pricingservice.dto.BaseFareResponse;
import com.rideshare.pricingservice.entity.PricingRule;
import com.rideshare.pricingservice.repository.PricingRuleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides base fare configurations with Redis caching.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BaseFareService {

    private final PricingRuleRepository pricingRuleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BASE_FARE_CACHE_PREFIX = "pricing:base_fare:";

    // Default pricing if no rule exists
    private static final BigDecimal DEFAULT_BASE_FARE = new BigDecimal("80.00");
    private static final BigDecimal DEFAULT_PER_KM = new BigDecimal("12.00");
    private static final BigDecimal DEFAULT_PER_MINUTE = new BigDecimal("1.50");
    private static final BigDecimal DEFAULT_MIN_FARE = new BigDecimal("50.00");
    private static final BigDecimal DEFAULT_BOOKING_FEE = new BigDecimal("10.00");
    private static final BigDecimal DEFAULT_CANCELLATION_FEE = new BigDecimal("50.00");

    /**
     * Gets the base fare configuration for a vehicle type and city.
     * Checks Redis cache first, falls back to database.
     */
    public BaseFareResponse getBaseFare(BaseFareRequest request) {
        String cacheKey = BASE_FARE_CACHE_PREFIX + request.getVehicleType() + ":" + request.getCity();

        // Try cache first
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return deserializeBaseFare(cached, request.getVehicleType(), request.getCity());
        }

        // Get from database
        Optional<PricingRule> rule = pricingRuleRepository
                .findByVehicleTypeAndCityAndActiveTrue(request.getVehicleType(), request.getCity());

        if (rule.isEmpty()) {
            rule = pricingRuleRepository.findByVehicleTypeAndActiveTrue(request.getVehicleType());
        }

        BaseFareResponse response;
        if (rule.isPresent()) {
            PricingRule r = rule.get();
            response = new BaseFareResponse(
                    r.getVehicleType(),
                    r.getCity(),
                    r.getBaseFare(),
                    r.getPerKmRate(),
                    r.getPerMinuteRate(),
                    r.getMinimumFare(),
                    r.getBookingFee(),
                    r.getCancellationFee());
        } else {
            response = new BaseFareResponse(
                    request.getVehicleType(),
                    request.getCity(),
                    DEFAULT_BASE_FARE,
                    DEFAULT_PER_KM,
                    DEFAULT_PER_MINUTE,
                    DEFAULT_MIN_FARE,
                    DEFAULT_BOOKING_FEE,
                    DEFAULT_CANCELLATION_FEE);
        }

        // Cache for 1 hour
        redisTemplate.opsForValue().set(cacheKey, serializeBaseFare(response),
                java.time.Duration.ofHours(1));

        return response;
    }

    private String serializeBaseFare(BaseFareResponse response) {
        return String.join("|",
                response.getBaseFare().toString(),
                response.getPerKmRate().toString(),
                response.getPerMinuteRate().toString(),
                response.getMinimumFare().toString(),
                response.getBookingFee().toString(),
                response.getCancellationFee().toString());
    }

    private BaseFareResponse deserializeBaseFare(String data, String vehicleType, String city) {
        String[] parts = data.split("\\|");
        return new BaseFareResponse(
                vehicleType, city,
                new BigDecimal(parts[0]),
                new BigDecimal(parts[1]),
                new BigDecimal(parts[2]),
                new BigDecimal(parts[3]),
                new BigDecimal(parts[4]),
                new BigDecimal(parts[5]));
    }
}
