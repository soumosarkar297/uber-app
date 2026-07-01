package com.rideshare.driverservice.controller;

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

import com.rideshare.driverservice.context.DriverContext;
import com.rideshare.driverservice.dto.ApiResponse;
import com.rideshare.driverservice.dto.DriverProfileResponse;
import com.rideshare.driverservice.dto.DriverProfileUpdateRequest;
import com.rideshare.driverservice.dto.DriverRegistrationRequest;
import com.rideshare.driverservice.dto.RegistrationResponse;
import com.rideshare.driverservice.entity.Driver;
import com.rideshare.driverservice.service.DriverService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for driver registration, profile management, and availability operations.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Management", description = "Driver registration, profile, and availability management")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    @Operation(summary = "Register Driver", description = "Registers a new driver account")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerDriver(@Valid @RequestBody DriverRegistrationRequest request) {
        log.info("Registering new driver with phone: {}", request.getPhoneNumber());
        RegistrationResponse response = driverService.registerDriver(request);
        return ResponseEntity.status(201).body(ApiResponse.success("Driver registered successfully", response));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get Driver Profile", description = "Returns the authenticated driver's profile")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> getProfile() {
        Driver currentDriver = DriverContext.getCurrentDriver();
        if (currentDriver == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID driverId = currentDriver.getId();
        log.info("Getting profile for driver: {}", driverId);
        return driverService.getDriverProfile(driverId)
                .map(profile -> ResponseEntity.ok(ApiResponse.success(profile)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Driver not found", "DRIVER_NOT_FOUND")));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Driver by ID", description = "Returns a driver's profile by their UUID")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> getDriverById(@PathVariable UUID id) {
        log.info("Getting driver by ID: {}", id);
        return driverService.getDriverProfile(id)
                .map(profile -> ResponseEntity.ok(ApiResponse.success(profile)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Driver not found", "DRIVER_NOT_FOUND")));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update Driver Profile", description = "Updates the authenticated driver's profile")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> updateProfile(
            @Valid @RequestBody DriverProfileUpdateRequest request) {
        Driver currentDriver = DriverContext.getCurrentDriver();
        if (currentDriver == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID driverId = currentDriver.getId();
        log.info("Updating profile for driver: {}", driverId);
        try {
            DriverProfileResponse updatedProfile = driverService.updateDriverProfile(driverId, request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/availability")
    @Operation(summary = "Update Availability", description = "Sets driver availability status")
    public ResponseEntity<ApiResponse<String>> updateAvailability(@RequestParam boolean available) {
        Driver currentDriver = DriverContext.getCurrentDriver();
        if (currentDriver == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID driverId = currentDriver.getId();
        log.info("Setting availability for driver {} to: {}", driverId, available);
        try {
            driverService.setAvailability(driverId, available);
            return ResponseEntity.ok(ApiResponse.success("Availability updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/location")
    @Operation(summary = "Update Location", description = "Updates the driver's current location")
    public ResponseEntity<ApiResponse<String>> updateLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        Driver currentDriver = DriverContext.getCurrentDriver();
        if (currentDriver == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID driverId = currentDriver.getId();
        log.info("Updating location for driver {}: lat={}, lon={}", driverId, latitude, longitude);
        try {
            driverService.updateLocation(driverId, latitude, longitude);
            return ResponseEntity.ok(ApiResponse.success("Location updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/online")
    @Operation(summary = "Set Online Status", description = "Sets driver online/offline status")
    public ResponseEntity<ApiResponse<String>> setOnlineStatus(@RequestParam boolean online) {
        Driver currentDriver = DriverContext.getCurrentDriver();
        if (currentDriver == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID driverId = currentDriver.getId();
        log.info("Setting online status for driver {} to: {}", driverId, online);
        try {
            driverService.setOnlineStatus(driverId, online);
            return ResponseEntity.ok(ApiResponse.success("Online status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }
}
