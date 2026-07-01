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
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.userservice.context.UserContext;
import com.rideshare.userservice.dto.ApiResponse;
import com.rideshare.userservice.dto.ProfileUpdateRequest;
import com.rideshare.userservice.dto.RegistrationResponse;
import com.rideshare.userservice.dto.RiderProfileResponse;
import com.rideshare.userservice.dto.RiderRegistrationRequest;
import com.rideshare.userservice.entity.User;
import com.rideshare.userservice.service.RiderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles rider registration and profile management endpoints.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/riders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rider Management", description = "Rider registration and profile management")
public class RiderController {

    private final RiderService riderService;

    @PostMapping("/register")
    @Operation(summary = "Register Rider", description = "Registers a new rider account")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerRider(@Valid @RequestBody RiderRegistrationRequest request) {
        log.info("Registering new rider with phone: {}", request.getPhoneNumber());
        RegistrationResponse response = riderService.registerRider(request);
        return ResponseEntity.status(201).body(ApiResponse.success("Rider registered successfully", response));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get Rider Profile", description = "Returns the authenticated rider's profile")
    public ResponseEntity<ApiResponse<RiderProfileResponse>> getProfile() {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Getting profile for rider: {}", userId);
        return riderService.getRiderProfile(userId)
                .map(profile -> ResponseEntity.ok(ApiResponse.success(profile)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Rider not found", "RIDER_NOT_FOUND")));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Rider by ID", description = "Returns a rider's profile by their UUID")
    public ResponseEntity<ApiResponse<RiderProfileResponse>> getRiderById(@PathVariable UUID id) {
        log.info("Getting rider by ID: {}", id);
        return riderService.getRiderProfile(id)
                .map(profile -> ResponseEntity.ok(ApiResponse.success(profile)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Rider not found", "RIDER_NOT_FOUND")));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update Rider Profile", description = "Updates the authenticated rider's profile")
    public ResponseEntity<ApiResponse<RiderProfileResponse>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request) {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Updating profile for rider: {}", userId);
        try {
            RiderProfileResponse updatedProfile = riderService.updateRiderProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }
}
