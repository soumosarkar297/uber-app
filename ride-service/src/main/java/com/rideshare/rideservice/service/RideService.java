package com.rideshare.rideservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.rideservice.dto.RideCancelRequest;
import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.event.RideAcceptedEvent;
import com.rideshare.rideservice.event.RideCancelledEvent;
import com.rideshare.rideservice.event.RideCompletedEvent;
import com.rideshare.rideservice.event.RideRequestedEvent;
import com.rideshare.rideservice.event.RideStartedEvent;
import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideStatus;
import com.rideshare.rideservice.repository.RideRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Core service orchestrating ride lifecycle operations and Kafka event publishing.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";
    private static final String RIDE_ACCEPTED_TOPIC = "ride.accepted";
    private static final String RIDE_CANCELLED_TOPIC = "ride.cancelled";
    private static final String RIDE_STARTED_TOPIC = "ride.started";
    private static final String RIDE_COMPLETED_TOPIC = "ride.completed";

    /**
     * Creates a new ride request from a rider.
     * Transitions: -> REQUESTED -> MATCHING
     */
    public RideResponse requestRide(RideRequest request) {
        log.info("New ride request from rider: {}", request.getRiderId());

        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropLatitude(request.getDropLatitude());
        ride.setDropLongitude(request.getDropLongitude());
        ride.setDropAddress(request.getDropAddress());
        ride.setVehicleType(request.getVehicleType());
        ride.setStatus(RideStatus.REQUESTED);

        Ride savedRide = rideRepository.save(ride);

        // Transition to MATCHING
        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);

        // Publish event to Kafka
        RideRequestedEvent event = new RideRequestedEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getPickupAddress(),
                savedRide.getDropLatitude(),
                savedRide.getDropLongitude(),
                savedRide.getDropAddress());

        kafkaTemplate.send(RIDE_REQUESTED_TOPIC, savedRide.getId(), event);
        log.info("RideRequestedEvent published for ride: {}", savedRide.getId());

        return mapToResponse(savedRide);
    }

    /**
     * Accepts a ride request. Called by matching service when driver accepts.
     * Transitions: MATCHING -> ACCEPTED
     */
    public RideResponse acceptRide(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.getStatus().validateTransition(RideStatus.ACCEPTED);

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());
        rideRepository.save(ride);

        log.info("Ride {} accepted by driver {}", rideId, driverId);

        // Publish accepted event
        RideAcceptedEvent event = new RideAcceptedEvent(
                rideId, ride.getRiderId(), driverId, 0, 0, 0);
        kafkaTemplate.send(RIDE_ACCEPTED_TOPIC, rideId, event);

        return mapToResponse(ride);
    }

    /**
     * Marks driver as arriving at pickup.
     * Transitions: ACCEPTED -> DRIVER_ARRIVING
     */
    public RideResponse driverArriving(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.getStatus().validateTransition(RideStatus.DRIVER_ARRIVING);
        ride.setStatus(RideStatus.DRIVER_ARRIVING);
        rideRepository.save(ride);

        log.info("Ride {} - driver arriving at pickup", rideId);
        return mapToResponse(ride);
    }

    /**
     * Starts a ride that has been accepted by a driver.
     * Transitions: DRIVER_ARRIVING -> RIDE_STARTED
     */
    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.getStatus().validateTransition(RideStatus.RIDE_STARTED);

        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);

        log.info("Ride {} started", rideId);

        RideStartedEvent event = new RideStartedEvent(
                rideId, ride.getRiderId(), ride.getDriverId());
        kafkaTemplate.send(RIDE_STARTED_TOPIC, rideId, event);

        return mapToResponse(ride);
    }

    /**
     * Completes a ride that is currently in progress.
     * Transitions: RIDE_STARTED -> COMPLETED
     */
    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.getStatus().validateTransition(RideStatus.COMPLETED);

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare());

        if (ride.getStartedAt() != null) {
            double durationMinutes = java.time.Duration.between(
                    ride.getStartedAt(), ride.getCompletedAt()).toMinutes();
            ride.setDurationMinutes(durationMinutes);
        }

        rideRepository.save(ride);

        log.info("Ride {} completed", rideId);

        RideCompletedEvent event = new RideCompletedEvent(
                rideId, ride.getRiderId(), ride.getDriverId(),
                ride.getActualFare(),
                ride.getDistanceKm() != null ? ride.getDistanceKm() : 0,
                ride.getDurationMinutes() != null ? ride.getDurationMinutes() : 0);
        kafkaTemplate.send(RIDE_COMPLETED_TOPIC, rideId, event);

        return mapToResponse(ride);
    }

    /**
     * Cancels a ride. Can be called from any state except COMPLETED.
     */
    public RideResponse cancelRide(String rideId, RideCancelRequest cancelRequest) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.getStatus().validateTransition(RideStatus.CANCELLED);

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(cancelRequest.getReason());
        ride.setCancelledBy(cancelRequest.getCancelledBy());
        ride.setCancelledAt(LocalDateTime.now());
        rideRepository.save(ride);

        log.info("Ride {} cancelled. Reason: {}", rideId, cancelRequest.getReason());

        RideCancelledEvent event = new RideCancelledEvent(
                rideId, ride.getRiderId(), ride.getDriverId(),
                cancelRequest.getReason().name(),
                cancelRequest.getCancelledBy());
        kafkaTemplate.send(RIDE_CANCELLED_TOPIC, rideId, event);

        return mapToResponse(ride);
    }

    /**
     * Legacy cancel method for backward compatibility.
     */
    public RideResponse cancelRide(String rideId) {
        RideCancelRequest request = new RideCancelRequest(
                com.rideshare.rideservice.model.RideCancellationReason.RIDER_CANCELLED,
                "rider");
        return cancelRide(rideId, request);
    }

    /**
     * Retrieves a ride by its unique identifier.
     */
    public RideResponse getRideById(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        return mapToResponse(ride);
    }

    /**
     * Retrieves all rides for a specific rider.
     */
    public List<RideResponse> getRidesByRider(String riderId) {
        return rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId)
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Retrieves active rides for a driver.
     */
    public List<RideResponse> getActiveRidesByDriver(String driverId) {
        return rideRepository.findByDriverIdAndStatusIn(driverId,
                List.of(RideStatus.ACCEPTED, RideStatus.DRIVER_ARRIVING, RideStatus.RIDE_STARTED))
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Sets estimated fare with surge multiplier.
     */
    public RideResponse setFareEstimate(String rideId, double estimatedFare, double surgeMultiplier) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setEstimatedFare(estimatedFare);
        ride.setSurgeMultiplier(surgeMultiplier);
        rideRepository.save(ride);
        return mapToResponse(ride);
    }

    private RideResponse mapToResponse(Ride ride) {
        RideResponse response = new RideResponse();
        response.setId(ride.getId());
        response.setRiderId(ride.getRiderId());
        response.setDriverId(ride.getDriverId());
        response.setPickupLatitude(ride.getPickupLatitude());
        response.setPickupLongitude(ride.getPickupLongitude());
        response.setPickupAddress(ride.getPickupAddress());
        response.setDropLatitude(ride.getDropLatitude());
        response.setDropLongitude(ride.getDropLongitude());
        response.setDropAddress(ride.getDropAddress());
        response.setStatus(ride.getStatus());
        response.setCancellationReason(ride.getCancellationReason());
        response.setCancelledBy(ride.getCancelledBy());
        response.setEstimatedFare(ride.getEstimatedFare());
        response.setActualFare(ride.getActualFare());
        response.setDistanceKm(ride.getDistanceKm());
        response.setDurationMinutes(ride.getDurationMinutes());
        response.setVehicleType(ride.getVehicleType());
        response.setSurgeMultiplier(ride.getSurgeMultiplier());
        response.setCreatedAt(ride.getCreatedAt());
        response.setUpdatedAt(ride.getUpdatedAt());
        response.setStartedAt(ride.getStartedAt());
        response.setCompletedAt(ride.getCompletedAt());
        response.setCancelledAt(ride.getCancelledAt());
        response.setAcceptedAt(ride.getAcceptedAt());
        response.setDriverArrivedAt(ride.getDriverArrivedAt());
        return response;
    }
}
