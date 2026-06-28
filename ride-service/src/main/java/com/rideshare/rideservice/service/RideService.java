package com.rideshare.rideservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.event.RideRequestedEvent;
import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideStatus;
import com.rideshare.rideservice.repository.RideRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for ride management operations.
 * Handles ride lifecycle: creation, matching, starting, completion, and cancellation.
 * Publishes ride events to Kafka for inter-service communication.
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
    private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;

    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";

    /**
     * Creates a new ride request from a rider.
     * Persists the ride with REQUESTED status, calculates estimated fare,
     * publishes RideRequestedEvent to Kafka for driver matching,
     * then updates status to MATCHING.
     *
     * @param request the ride request containing pickup and drop-off details
     * @return RideResponse with created ride details
     * @throws RuntimeException if ride creation or event publishing fails
     */
    public RideResponse requestRide(RideRequest request) {
        log.info("New ride request from rider: {}", request.getRiderId());

        // Step 1: save ride to database
        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropLatitude(request.getDropLatitude());
        ride.setDropLongitude(request.getDropLongitude());
        ride.setDropAddress(request.getDropAddress());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimateFare(request));

        Ride savedRide = rideRepository.save(ride);

        // Step 2: Publish event to Kafka
        // Matching service will consume this and find nearest driver
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
        log.info("RideRequestedEvent published to Kafka for ride: {}", savedRide.getId());

        // Update status to Matching
        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);

        return mapToResponse(savedRide);
    }

    /**
     * Updates a ride with the assigned driver ID.
     * Called by Matching Service when a driver accepts a ride.
     * Transitions ride status from MATCHING to ACCEPTED.
     *
     * @param rideId the unique identifier of the ride
     * @param driverId the unique identifier of the driver who accepted
     * @throws RuntimeException if ride not found
     */
    public void updateRideWithDriver(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    /**
     * Starts a ride that has been accepted by a driver.
     * Transitions ride status from ACCEPTED to RIDE_STARTED.
     * Records the start timestamp.
     *
     * @param rideId the unique identifier of the ride to start
     * @return RideResponse with updated ride details
     * @throws RuntimeException if ride not found or invalid status (must be ACCEPTED)
     */
    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride cannot be started. Current status: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);

        return mapToResponse(ride);
    }

    /**
     * Completes a ride that is currently in progress.
     * Transitions ride status from RIDE_STARTED to COMPLETED.
     * Records completion timestamp and sets actual fare equal to estimated fare.
     *
     * @param rideId the unique identifier of the ride to complete
     * @return RideResponse with updated ride details
     * @throws RuntimeException if ride not found or invalid status (must be RIDE_STARTED)
     */
    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride cannot be completed. Current status: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare());
        rideRepository.save(ride);

        return mapToResponse(ride);
    }

    /**
     * Cancels a ride at any stage before completion.
     * Transitions ride status to CANCELLED.
     * Can be called from any status except COMPLETED.
     *
     * @param rideId the unique identifier of the ride to cancel
     * @return RideResponse with updated ride details
     * @throws RuntimeException if ride not found
     */
    public RideResponse cancelRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);

        return mapToResponse(ride);
    }

    /**
     * Retrieves a ride by its unique identifier.
     *
     * @param rideId the unique identifier of the ride
     * @return RideResponse with ride details
     * @throws RuntimeException if ride not found
     */
    public RideResponse getRideById(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        return mapToResponse(ride);
    }

    /**
     * Retrieves all rides for a specific rider, ordered by creation date descending.
     *
     * @param riderId the unique identifier of the rider
     * @return list of RideResponse objects ordered by createdAt descending
     */
    public List<RideResponse> getRidesByRider(String riderId) {
        List<Ride> rides = rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId);
        return rides.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Maps a Ride entity to a RideResponse DTO.
     *
     * @param ride the Ride entity to map
     * @return RideResponse with all ride details
     */
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
        response.setEstimatedFare(ride.getEstimatedFare());
        response.setActualFare(ride.getActualFare());
        response.setCreatedAt(ride.getCreatedAt());
        response.setUpdatedAt(ride.getUpdatedAt());
        response.setStartedAt(ride.getStartedAt());
        response.setCompletedAt(ride.getCompletedAt());
        return response;
    }

    /**
     * Calculates estimated fare based on distance between pickup and drop-off locations.
     * Uses Haversine formula to calculate distance in kilometers.
     * Fare = BASE_FARE + (distance_km * PER_KM_RATE)
     *
     * @param request the ride request containing pickup and drop coordinates
     * @return estimated fare in the local currency
     */
    private double calculateEstimateFare(RideRequest request) {
        double distanceKm = calculateDistance(
                request.getPickupLatitude(),
                request.getPickupLongitude(),
                request.getDropLatitude(),
                request.getDropLongitude());

        // Fare calculation: base fare + per km rate
        final double BASE_FARE = 80.0; // Base fare in Rupee (₹)
        final double PER_KM_RATE = 20.0; // Rate per kilometer

        return BASE_FARE + (distanceKm * PER_KM_RATE);
    }

    /**
     * Calculates distance between two geographic coordinates using Haversine formula.
     *
     * @param lat1 latitude of first point in degrees
     * @param lon1 longitude of first point in degrees
     * @param lat2 latitude of second point in degrees
     * @param lon2 longitude of second point in degrees
     * @return distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
