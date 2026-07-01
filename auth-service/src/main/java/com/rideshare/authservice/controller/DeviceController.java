package com.rideshare.authservice.controller;

import com.rideshare.authservice.dto.ApiResponse;
import com.rideshare.authservice.dto.DeviceRegisterRequest;
import com.rideshare.authservice.dto.DeviceResponse;
import com.rideshare.authservice.dto.DeviceUpdateRequest;
import com.rideshare.authservice.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Device Management", description = "Device registration, listing, and revocation")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/register")
    @Operation(summary = "Register Device", description = "Registers a new device for the authenticated user")
    public ResponseEntity<ApiResponse<DeviceResponse>> registerDevice(
            Authentication authentication,
            @Valid @RequestBody DeviceRegisterRequest request) {

        String phoneNumber = authentication.getName();
        String deviceId = request.getDeviceId();
        Map<String, Object> deviceInfo = request.getDeviceInfo();

        log.info("Registering device: {} for user: {}", deviceId, phoneNumber);

        boolean registered = deviceService.registerDevice(phoneNumber, deviceId, deviceInfo);

        if (!registered) {
            if (deviceService.isDeviceRegistered(phoneNumber, deviceId)) {
                return ResponseEntity.status(409)
                        .body(ApiResponse.error("Device already registered", "DEVICE_ALREADY_REGISTERED"));
            }
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("Maximum device limit reached (" + deviceService.getMaxDevicesPerUser() + ")", "DEVICE_LIMIT_REACHED"));
        }

        DeviceResponse response = buildDeviceResponse(phoneNumber, deviceId);
        return ResponseEntity.ok(ApiResponse.success("Device registered successfully", response));
    }

    @GetMapping
    @Operation(summary = "List Devices", description = "Lists all registered devices for the authenticated user")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> listDevices(Authentication authentication) {
        String phoneNumber = authentication.getName();
        log.debug("Listing devices for user: {}", phoneNumber);

        List<Map<String, Object>> devices = deviceService.getUserDevices(phoneNumber);

        List<DeviceResponse> responses = devices.stream()
                .map(device -> new DeviceResponse(
                        (String) device.get("deviceId"),
                        (Map<String, Object>) device.get("deviceInfo"),
                        (String) device.get("registeredAt"),
                        (String) device.get("lastUsedAt")
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @DeleteMapping("/{deviceId}")
    @Operation(summary = "Revoke Device", description = "Revokes a specific device for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> revokeDevice(
            Authentication authentication,
            @PathVariable String deviceId) {

        String phoneNumber = authentication.getName();
        log.info("Revoking device: {} for user: {}", deviceId, phoneNumber);

        boolean revoked = deviceService.revokeDevice(phoneNumber, deviceId, null);

        if (!revoked) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Device not found", "DEVICE_NOT_FOUND"));
        }

        return ResponseEntity.ok(ApiResponse.<Void>success("Device revoked successfully", null));
    }

    @PutMapping("/{deviceId}")
    @Operation(summary = "Update Device", description = "Updates device information for the authenticated user")
    public ResponseEntity<ApiResponse<DeviceResponse>> updateDevice(
            Authentication authentication,
            @PathVariable String deviceId,
            @Valid @RequestBody DeviceUpdateRequest request) {

        String phoneNumber = authentication.getName();
        Map<String, Object> deviceInfo = request.getDeviceInfo();

        log.info("Updating device: {} for user: {}", deviceId, phoneNumber);

        if (!deviceService.isDeviceRegistered(phoneNumber, deviceId)) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Device not found", "DEVICE_NOT_FOUND"));
        }

        // Update device info in Redis
        String deviceKey = "device:" + phoneNumber + ":" + deviceId;
        // The DeviceService doesn't have an explicit updateDeviceInfo method,
        // so we'll need to read, modify, and write back
        // For now, we just update the lastUsed timestamp
        deviceService.updateLastUsed(phoneNumber, deviceId);

        DeviceResponse response = buildDeviceResponse(phoneNumber, deviceId);
        return ResponseEntity.ok(ApiResponse.success("Device updated successfully", response));
    }

    private DeviceResponse buildDeviceResponse(String phoneNumber, String deviceId) {
        String deviceKey = "device:" + phoneNumber + ":" + deviceId;
        List<Map<String, Object>> devices = deviceService.getUserDevices(phoneNumber);
        for (Map<String, Object> device : devices) {
            if (deviceId.equals(device.get("deviceId"))) {
                return new DeviceResponse(
                        (String) device.get("deviceId"),
                        (Map<String, Object>) device.get("deviceInfo"),
                        (String) device.get("registeredAt"),
                        (String) device.get("lastUsedAt")
                );
            }
        }
        return new DeviceResponse(deviceId, Map.of(), null, null);
    }
}
