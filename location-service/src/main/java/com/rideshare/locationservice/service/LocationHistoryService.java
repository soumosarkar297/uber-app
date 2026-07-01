package com.rideshare.locationservice.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.LocationHistoryEntry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages driver location history storage and retrieval using Redis.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LocationHistoryService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOCATION_HISTORY_PREFIX = "location:history:";
    private static final long HISTORY_TTL_HOURS = 24;
    private static final int MAX_HISTORY_POINTS = 1000;

    /**
     * Records a location point in the driver's history.
     * Stored as a Redis sorted set with timestamp as score.
     */
    public void recordLocation(String driverId, double latitude, double longitude,
                                Double heading, Double speed) {
        String key = LOCATION_HISTORY_PREFIX + driverId;
        long timestamp = Instant.now().toEpochMilli();

        String value = String.format("%.6f,%.6f,%d,%s,%s",
                latitude, longitude, timestamp,
                heading != null ? heading.toString() : "",
                speed != null ? speed.toString() : "");

        redisTemplate.opsForZSet().add(key, value, timestamp);

        // Trim to max points
        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size != null && size > MAX_HISTORY_POINTS) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - MAX_HISTORY_POINTS - 1);
        }

        // Set TTL
        redisTemplate.expire(key, java.time.Duration.ofHours(HISTORY_TTL_HOURS));
    }

    /**
     * Retrieves location history for a driver within a time range.
     */
    public List<LocationHistoryEntry> getHistory(String driverId,
                                                  LocalDateTime from,
                                                  LocalDateTime to) {
        String key = LOCATION_HISTORY_PREFIX + driverId;
        double fromScore = from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        double toScore = to.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Set<String> entries = redisTemplate.opsForZSet().rangeByScore(key, fromScore, toScore);

        List<LocationHistoryEntry> history = new ArrayList<>();
        if (entries != null) {
            for (String entry : entries) {
                history.add(parseHistoryEntry(entry));
            }
        }
        return history;
    }

    /**
     * Gets the most recent location for a driver from history.
     */
    public LocationHistoryEntry getLatestLocation(String driverId) {
        String key = LOCATION_HISTORY_PREFIX + driverId;
        Set<String> entries = redisTemplate.opsForZSet().reverseRange(key, 0, 0);

        if (entries != null && !entries.isEmpty()) {
            return parseHistoryEntry(entries.iterator().next());
        }
        return null;
    }

    private LocationHistoryEntry parseHistoryEntry(String entry) {
        String[] parts = entry.split(",");
        LocationHistoryEntry historyEntry = new LocationHistoryEntry();
        historyEntry.setLatitude(Double.parseDouble(parts[0]));
        historyEntry.setLongitude(Double.parseDouble(parts[1]));
        historyEntry.setTimestamp(Instant.ofEpochMilli(Long.parseLong(parts[2]))
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
        if (parts.length > 3 && !parts[3].isEmpty()) {
            historyEntry.setHeading(Double.parseDouble(parts[3]));
        }
        if (parts.length > 4 && !parts[4].isEmpty()) {
            historyEntry.setSpeed(Double.parseDouble(parts[4]));
        }
        return historyEntry;
    }
}
