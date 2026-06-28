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

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.service.RideService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for ride management operations.
 * Provides endpoints for creating rides, managing ride lifecycle,
 * and retrieving ride information.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    /**
     * Creates a new ride request from a rider.
     * Publishes a RideRequestedEvent to Kafka for driver matching.
     *
     * @param rideRequest the ride request containing pickup and drop-off details
     * @return ResponseEntity with the created ride response and HTTP 201 status
     * @throws RuntimeException if ride creation fails
     */
    @PostMapping("/request")
    public ResponseEntity<RideResponse> requestRide(
            @Valid @RequestBody RideRequest rideRequest
    ) {
        log.info("Ride request received from rider: {}", rideRequest.getRiderId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rideService.requestRide(rideRequest));
    }

    /**
     * Starts a ride that has been accepted by a driver.
     * Transitions ride status from ACCEPTED to RIDE_STARTED.
     *
     * @param rideId the unique identifier of the ride to start
     * @return ResponseEntity with the updated ride response
     * @throws RuntimeException if ride not found or invalid status transition
     */
    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideResponse> startRide(
            @PathVariable String rideId
    ) {
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    /**
     * Completes a ride that is currently in progress.
     * Transitions ride status from RIDE_STARTED to COMPLETED.
     * Sets actual fare equal to estimated fare.
     *
     * @param rideId the unique identifier of the ride to complete
     * @return ResponseEntity with the updated ride response
     * @throws RuntimeException if ride not found or invalid status transition
     */
    @PutMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(
            @PathVariable String rideId
    ) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    /**
     * Cancels a ride at any stage before completion.
     * Transitions ride status to CANCELLED.
     *
     * @param rideId the unique identifier of the ride to cancel
     * @return ResponseEntity with the updated ride response
     * @throws RuntimeException if ride not found
     */
    @PutMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable String rideId
    ) {
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }

    /**
     * Retrieves a ride by its unique identifier.
     *
     * @param rideId the unique identifier of the ride
     * @return ResponseEntity with the ride response
     * @throws RuntimeException if ride not found
     */
    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(
            @PathVariable String rideId
    ) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    /**
     * Retrieves all rides for a specific rider, ordered by creation date descending.
     *
     * @param riderId the unique identifier of the rider
     * @return ResponseEntity with list of ride responses
     * @throws RuntimeException if rider not found
     */
    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponse>> getRidesByRider(
            @PathVariable String riderId
    ) {
        return ResponseEntity.ok(rideService.getRidesByRider(riderId));
    }

}
