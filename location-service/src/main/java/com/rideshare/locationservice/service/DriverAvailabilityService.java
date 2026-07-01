package com.rideshare.locationservice.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.NearByDriverResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages driver availability status using Redis.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DriverAvailabilityService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String DRIVER_AVAILABILITY_KEY = "drivers:available";
    private static final String DRIVER_ZONE_PREFIX = "drivers:zone:";
    private static final String ACTIVE_RIDES_PREFIX = "rides:active:";

    /**
     * Sets driver availability status.
     */
    public void setAvailability(String driverId, boolean available, String zone) {
        if (available) {
            redisTemplate.opsForSet().add(DRIVER_AVAILABILITY_KEY, driverId);
            if (zone != null) {
                redisTemplate.opsForValue().set(DRIVER_ZONE_PREFIX + driverId, zone,
                        java.time.Duration.ofHours(24));
            }
        } else {
            redisTemplate.opsForSet().remove(DRIVER_AVAILABILITY_KEY, driverId);
            redisTemplate.delete(DRIVER_ZONE_PREFIX + driverId);
        }
    }

    /**
     * Checks if a driver is available.
     */
    public boolean isAvailable(String driverId) {
        Boolean isMember = redisTemplate.opsForSet().isMember(DRIVER_AVAILABILITY_KEY, driverId);
        return Boolean.TRUE.equals(isMember);
    }

    /**
     * Gets all available drivers.
     */
    public List<String> getAvailableDrivers() {
        return redisTemplate.opsForSet().members(DRIVER_AVAILABILITY_KEY)
                .stream().toList();
    }

    /**
     * Gets the driver's current zone.
     */
    public String getDriverZone(String driverId) {
        return redisTemplate.opsForValue().get(DRIVER_ZONE_PREFIX + driverId);
    }

    /**
     * Gets the count of available drivers in a zone.
     */
    public long getAvailableDriverCountInZone(String zone) {
        List<String> availableDrivers = getAvailableDrivers();
        return availableDrivers.stream()
                .filter(driverId -> zone.equals(getDriverZone(driverId)))
                .count();
    }

    /**
     * Records an active ride for a driver (marks them as unavailable).
     */
    public void markDriverBusy(String driverId, String rideId) {
        redisTemplate.opsForValue().set(ACTIVE_RIDES_PREFIX + driverId, rideId,
                java.time.Duration.ofHours(8));
        setAvailability(driverId, false, null);
    }

    /**
     * Marks driver as available after ride completion.
     */
    public void markDriverFree(String driverId) {
        redisTemplate.delete(ACTIVE_RIDES_PREFIX + driverId);
        setAvailability(driverId, true, null);
    }
}
