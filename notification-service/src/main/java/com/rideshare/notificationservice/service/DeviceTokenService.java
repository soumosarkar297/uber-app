package com.rideshare.notificationservice.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.notificationservice.dto.RegisterDeviceRequest;
import com.rideshare.notificationservice.entity.DeviceToken;
import com.rideshare.notificationservice.repository.DeviceTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages device token registration and lookup for push notifications.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String DEVICE_TOKEN_PREFIX = "device:token:";

    /**
     * Registers or updates a device token for push notifications.
     */
    public void registerDevice(RegisterDeviceRequest request) {
        var existing = deviceTokenRepository.findByTokenAndActiveTrue(request.getToken());

        if (existing.isPresent()) {
            DeviceToken token = existing.get();
            token.setLastUsedAt(java.time.LocalDateTime.now());
            deviceTokenRepository.save(token);
            return;
        }

        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setUserId(request.getUserId());
        deviceToken.setToken(request.getToken());
        deviceToken.setPlatform(request.getPlatform());
        deviceToken.setActive(true);
        deviceTokenRepository.save(deviceToken);

        // Cache token in Redis for fast lookup
        redisTemplate.opsForValue().set(
                DEVICE_TOKEN_PREFIX + request.getUserId(),
                request.getToken(),
                java.time.Duration.ofHours(24));

        log.info("Device registered for user: {} platform: {}", request.getUserId(),
                request.getPlatform());
    }

    /**
     * Retrieves all active device tokens for a given user.
     */
    public List<String> getUserTokens(String userId) {
        return deviceTokenRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(DeviceToken::getToken)
                .toList();
    }

    /**
     * Deactivates a device token so it no longer receives notifications.
     */
    public void deactivateDevice(String token) {
        deviceTokenRepository.findByTokenAndActiveTrue(token).ifPresent(dt -> {
            dt.setActive(false);
            deviceTokenRepository.save(dt);
        });
    }
}
