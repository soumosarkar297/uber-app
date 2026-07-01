package com.rideshare.userservice.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.userservice.context.UserContext;
import com.rideshare.userservice.dto.ApiResponse;
import com.rideshare.userservice.dto.DriverProfileResponse;
import com.rideshare.userservice.dto.DriverRegistrationRequest;
import com.rideshare.userservice.dto.ProfileUpdateRequest;
import com.rideshare.userservice.dto.RegistrationResponse;
import com.rideshare.userservice.entity.User;
import com.rideshare.userservice.service.DriverService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerDriver(@Valid @RequestBody DriverRegistrationRequest request) {
        log.info("Registering new driver with phone: {}", request.getPhoneNumber());
        RegistrationResponse response = driverService.registerDriver(request);
        return ResponseEntity.status(201).body(ApiResponse.success("Driver registered successfully", response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> getProfile() {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Getting profile for driver: {}", userId);
        return driverService.getDriverProfile(userId)
                .map(profile -> ResponseEntity.ok(ApiResponse.success(profile)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Driver not found", "DRIVER_NOT_FOUND")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> getDriverById(@PathVariable UUID id) {
        log.info("Getting driver by ID: {}", id);
        return driverService.getDriverProfile(id)
                .map(profile -> ResponseEntity.ok(ApiResponse.success(profile)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Driver not found", "DRIVER_NOT_FOUND")));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request) {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Updating profile for driver: {}", userId);
        try {
            DriverProfileResponse updatedProfile = driverService.updateDriverProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/availability")
    public ResponseEntity<ApiResponse<String>> updateAvailability(@RequestParam boolean available) {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Setting availability for driver {} to: {}", userId, available);
        try {
            driverService.setAvailability(userId, available);
            return ResponseEntity.ok(ApiResponse.success("Availability updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/location")
    public ResponseEntity<ApiResponse<String>> updateLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Updating location for driver {}: lat={}, lon={}", userId, latitude, longitude);
        try {
            driverService.updateLocation(userId, latitude, longitude);
            return ResponseEntity.ok(ApiResponse.success("Location updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/online")
    public ResponseEntity<ApiResponse<String>> setOnlineStatus(@RequestParam boolean online) {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Setting online status for driver {} to: {}", userId, online);
        try {
            driverService.setOnlineStatus(userId, online);
            return ResponseEntity.ok(ApiResponse.success("Online status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }
}
