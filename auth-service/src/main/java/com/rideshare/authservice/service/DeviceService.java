package com.rideshare.authservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service for device management including registration, validation, listing, and revocation.
 * Enforces device limits and token binding to registered devices.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class DeviceService {

    private static final String DEVICE_KEY_PREFIX = "device:";
    private static final String USER_DEVICES_KEY_PREFIX = "devices:";
    private static final String REGISTERED_AT_FIELD = "registeredAt";
    private static final String LAST_USED_AT_FIELD = "lastUsedAt";
    private static final String DEVICE_INFO_FIELD = "deviceInfo";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final int maxDevicesPerUser;
    private final boolean tokenBindingEnabled;

    public DeviceService(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${device.max-devices-per-user:5}") int maxDevicesPerUser,
            @Value("${device.token-binding-enabled:true}") boolean tokenBindingEnabled) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.maxDevicesPerUser = maxDevicesPerUser;
        this.tokenBindingEnabled = tokenBindingEnabled;
    }

    /**
     * Registers a new device for a user.
     * Enforces maximum device limit per user.
     *
     * @param phoneNumber the user's phone number
     * @param deviceId the unique device identifier
     * @param deviceInfo additional device information (platform, model, appVersion, etc.)
     * @return true if device was registered, false if device already exists or limit exceeded
     */
    public boolean registerDevice(String phoneNumber, String deviceId, Map<String, Object> deviceInfo) {
        String deviceKey = getDeviceKey(phoneNumber, deviceId);
        String userDevicesKey = getUserDevicesKey(phoneNumber);

        // Check if device already registered
        if (Boolean.TRUE.equals(redisTemplate.hasKey(deviceKey))) {
            return false;
        }

        // Check device limit
        Long currentCount = redisTemplate.opsForSet().size(userDevicesKey);
        if (currentCount != null && currentCount >= maxDevicesPerUser) {
            return false;
        }

        // Create device record
        Map<String, Object> deviceRecord = new HashMap<>();
        deviceRecord.put(REGISTERED_AT_FIELD, Instant.now().toString());
        deviceRecord.put(LAST_USED_AT_FIELD, Instant.now().toString());
        deviceRecord.put(DEVICE_INFO_FIELD, deviceInfo != null ? deviceInfo : Collections.emptyMap());

        // Store device record and add to user's device set
        redisTemplate.opsForValue().set(deviceKey, deviceRecord);
        redisTemplate.opsForSet().add(userDevicesKey, deviceId);

        return true;
    }

    /**
     * Validates that a device is registered for the user and matches the provided deviceId.
     * If token binding is enabled and deviceId doesn't match, throws an exception.
     *
     * @param phoneNumber the user's phone number
     * @param deviceId the device identifier to validate
     * @return true if device is registered and matches
     * @throws IllegalStateException if token binding is enabled and device is not registered or doesn't match
     */
    public boolean validateDeviceBinding(String phoneNumber, String deviceId) {
        if (!tokenBindingEnabled) {
            return true;
        }

        boolean isRegistered = isDeviceRegistered(phoneNumber, deviceId);
        if (!isRegistered) {
            throw new IllegalStateException("Device not registered for user: " + phoneNumber + ", deviceId: " + deviceId);
        }
        return true;
    }

    /**
     * Retrieves all registered devices for a user with their information.
     *
     * @param phoneNumber the user's phone number
     * @return list of device info maps containing deviceId, deviceInfo, registeredAt, lastUsedAt
     */
    public List<Map<String, Object>> getUserDevices(String phoneNumber) {
        String userDevicesKey = getUserDevicesKey(phoneNumber);
        Set<Object> deviceIds = redisTemplate.opsForSet().members(userDevicesKey);

        if (deviceIds == null || deviceIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> devices = new ArrayList<>();
        for (Object deviceIdObj : deviceIds) {
            String deviceId = deviceIdObj.toString();
            String deviceKey = getDeviceKey(phoneNumber, deviceId);
            Object deviceRecordObj = redisTemplate.opsForValue().get(deviceKey);

            if (deviceRecordObj != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> deviceRecord = (Map<String, Object>) deviceRecordObj;

                Map<String, Object> deviceInfo = new HashMap<>();
                deviceInfo.put("deviceId", deviceId);
                deviceInfo.put("deviceInfo", deviceRecord.get(DEVICE_INFO_FIELD));
                deviceInfo.put("registeredAt", deviceRecord.get(REGISTERED_AT_FIELD));
                deviceInfo.put("lastUsedAt", deviceRecord.get(LAST_USED_AT_FIELD));

                devices.add(deviceInfo);
            }
        }

        return devices;
    }

    /**
     * Revokes a specific device for a user.
     * Removes device from registry and blacklists its associated tokens.
     *
     * @param phoneNumber the user's phone number
     * @param deviceId the device identifier to revoke
     * @param jwtService the JWT service for token blacklisting
     * @return true if device was revoked, false if device was not found
     */
    public boolean revokeDevice(String phoneNumber, String deviceId, JwtService jwtService) {
        String deviceKey = getDeviceKey(phoneNumber, deviceId);
        String userDevicesKey = getUserDevicesKey(phoneNumber);

        // Check if device exists
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(deviceKey))) {
            return false;
        }

        // Remove device from registry
        redisTemplate.delete(deviceKey);
        redisTemplate.opsForSet().remove(userDevicesKey, deviceId);

        // Note: Token blacklisting for specific device tokens would require
        // tracking token hashes per device. For now, we rely on token validation
        // to check device registration status at request time.
        // In a production system, you might store token hashes per device for immediate revocation.

        return true;
    }

    /**
     * Updates the last used timestamp for a device.
     *
     * @param phoneNumber the user's phone number
     * @param deviceId the device identifier
     * @return true if device exists and was updated, false otherwise
     */
    public boolean updateLastUsed(String phoneNumber, String deviceId) {
        String deviceKey = getDeviceKey(phoneNumber, deviceId);
        Object deviceRecordObj = redisTemplate.opsForValue().get(deviceKey);

        if (deviceRecordObj == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> deviceRecord = (Map<String, Object>) deviceRecordObj;
        deviceRecord.put(LAST_USED_AT_FIELD, Instant.now().toString());
        redisTemplate.opsForValue().set(deviceKey, deviceRecord);

        return true;
    }

    /**
     * Checks if a device is registered for a user.
     *
     * @param phoneNumber the user's phone number
     * @param deviceId the device identifier
     * @return true if device is registered, false otherwise
     */
    public boolean isDeviceRegistered(String phoneNumber, String deviceId) {
        String deviceKey = getDeviceKey(phoneNumber, deviceId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(deviceKey));
    }

    /**
     * Returns the count of registered devices for a user.
     *
     * @param phoneNumber the user's phone number
     * @return the number of registered devices
     */
    public long getDeviceCount(String phoneNumber) {
        String userDevicesKey = getUserDevicesKey(phoneNumber);
        Long count = redisTemplate.opsForSet().size(userDevicesKey);
        return count != null ? count : 0L;
    }

    /**
     * Checks if the user has reached the maximum device limit.
     *
     * @param phoneNumber the user's phone number
     * @return true if device limit reached, false otherwise
     */
    public boolean isDeviceLimitReached(String phoneNumber) {
        return getDeviceCount(phoneNumber) >= maxDevicesPerUser;
    }

    /**
     * Gets the maximum allowed devices per user.
     *
     * @return the maximum devices per user
     */
    public int getMaxDevicesPerUser() {
        return maxDevicesPerUser;
    }

    /**
     * Checks if token binding is enabled.
     *
     * @return true if token binding is enabled, false otherwise
     */
    public boolean isTokenBindingEnabled() {
        return tokenBindingEnabled;
    }

    /**
     * Constructs the Redis key for a specific device.
     */
    private String getDeviceKey(String phoneNumber, String deviceId) {
        return DEVICE_KEY_PREFIX + phoneNumber + ":" + deviceId;
    }

    /**
     * Constructs the Redis key for a user's device set.
     */
    private String getUserDevicesKey(String phoneNumber) {
        return USER_DEVICES_KEY_PREFIX + phoneNumber;
    }
}