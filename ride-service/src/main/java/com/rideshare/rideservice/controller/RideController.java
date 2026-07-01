package com.rideshare.rideservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.rideservice.dto.RideCancelRequest;
import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.service.RideService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing the full ride lifecycle from request to completion.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/rides")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Ride Management", description = "Ride requests, lifecycle, accept/decline, and history")
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    @Operation(summary = "Request Ride", description = "Creates a new ride request and publishes for driver matching")
    public ResponseEntity<RideResponse> requestRide(
            @Valid @RequestBody RideRequest rideRequest) {
        log.info("Ride request received from rider: {}", rideRequest.getRiderId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rideService.requestRide(rideRequest));
    }

    @PutMapping("/{rideId}/accept")
    @Operation(summary = "Accept Ride", description = "Driver accepts a ride request (MATCHING -> ACCEPTED)")
    public ResponseEntity<RideResponse> acceptRide(
            @PathVariable String rideId,
            @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(rideService.acceptRide(rideId, body.get("driverId")));
    }

    @PutMapping("/{rideId}/driver-arriving")
    @Operation(summary = "Driver Arriving", description = "Driver is en route to pickup (ACCEPTED -> DRIVER_ARRIVING)")
    public ResponseEntity<RideResponse> driverArriving(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.driverArriving(rideId));
    }

    @PutMapping("/{rideId}/start")
    @Operation(summary = "Start Ride", description = "Starts a ride (DRIVER_ARRIVING -> RIDE_STARTED)")
    public ResponseEntity<RideResponse> startRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    @PutMapping("/{rideId}/complete")
    @Operation(summary = "Complete Ride", description = "Completes a ride (RIDE_STARTED -> COMPLETED)")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @PutMapping("/{rideId}/cancel")
    @Operation(summary = "Cancel Ride", description = "Cancels a ride with a reason (any state -> CANCELLED)")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable String rideId,
            @Valid @RequestBody RideCancelRequest cancelRequest) {
        return ResponseEntity.ok(rideService.cancelRide(rideId, cancelRequest));
    }

    @GetMapping("/{rideId}")
    @Operation(summary = "Get Ride", description = "Retrieves a ride by its ID")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/rider/{riderId}")
    @Operation(summary = "Get Rider's Rides", description = "Retrieves all rides for a specific rider")
    public ResponseEntity<List<RideResponse>> getRidesByRider(
            @PathVariable String riderId) {
        return ResponseEntity.ok(rideService.getRidesByRider(riderId));
    }

    @GetMapping("/driver/{driverId}/active")
    @Operation(summary = "Get Driver's Active Rides", description = "Retrieves active rides for a driver")
    public ResponseEntity<List<RideResponse>> getActiveRidesByDriver(
            @PathVariable String driverId) {
        return ResponseEntity.ok(rideService.getActiveRidesByDriver(driverId));
    }
}
