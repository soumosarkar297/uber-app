package com.rideshare.userservice.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.userservice.dto.ProfileUpdateRequest;
import com.rideshare.userservice.dto.RegistrationResponse;
import com.rideshare.userservice.dto.RiderProfileResponse;
import com.rideshare.userservice.dto.RiderRegistrationRequest;
import com.rideshare.userservice.entity.Rider;
import com.rideshare.userservice.entity.UserType;
import com.rideshare.userservice.entity.VerificationStatus;
import com.rideshare.userservice.event.UserRegisteredEvent;
import com.rideshare.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for rider-specific operations.
 * Handles rider registration, profile management, and rider-specific queries.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiderService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserEventPublisher eventPublisher;

    /**
     * Register a new rider.
     * Creates both User and Rider entities.
     */
    @Transactional
    public RegistrationResponse registerRider(RiderRegistrationRequest request) {
        // Validate phone number and email don't exist
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create Rider entity (extends User)
        Rider rider = new Rider();
        rider.setFirstName(request.getFirstName());
        rider.setLastName(request.getLastName());
        rider.setPhoneNumber(request.getPhoneNumber());
        rider.setEmail(request.getEmail());
        rider.setProfileImageUrl(request.getProfileImageUrl());
        rider.setUserType(UserType.RIDER);
        rider.setVerificationStatus(VerificationStatus.PENDING);
        rider.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
        rider.setTotalRides(0);
        rider.setTotalSpent(0.0);
        rider.setRating(5.0);
        rider.setIsActive(true);

        Rider savedRider = userRepository.save(rider);

        // Publish user registered event
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(savedRider.getId())
                .phoneNumber(savedRider.getPhoneNumber())
                .userType(UserType.RIDER.name())
                .timestamp(java.time.Instant.now())
                .build();
        eventPublisher.publishUserRegistered(event);

        return new RegistrationResponse(
                savedRider.getId(),
                UserType.RIDER.name(),
                VerificationStatus.PENDING.name(),
                "Rider registered successfully. Verification pending."
        );
    }

    /**
     * Get rider profile by ID.
     */
    public Optional<RiderProfileResponse> getRiderProfile(UUID riderId) {
        return userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .map(this::mapToRiderProfileResponse);
    }

    /**
     * Get rider profile by phone number.
     */
    public Optional<RiderProfileResponse> getRiderProfileByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumberAndUserType(phoneNumber, UserType.RIDER)
                .map(user -> (Rider) user)
                .map(this::mapToRiderProfileResponse);
    }

    /**
     * Update rider profile.
     */
    @Transactional
    public RiderProfileResponse updateRiderProfile(UUID riderId, ProfileUpdateRequest request) {
        Rider rider = userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        // Update common fields
        if (request.getFirstName() != null) {
            rider.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            rider.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail()) && !rider.getEmail().equals(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            rider.setEmail(request.getEmail());
        }
        if (request.getProfileImageUrl() != null) {
            rider.setProfileImageUrl(request.getProfileImageUrl());
        }

        // Update rider-specific fields
        if (request.getPreferredPaymentMethod() != null) {
            rider.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
        }

        Rider updatedRider = userRepository.save(rider);
        return mapToRiderProfileResponse(updatedRider);
    }

    /**
     * Update rider verification status.
     */
    @Transactional
    public Rider updateVerificationStatus(UUID riderId, VerificationStatus status) {
        Rider rider = userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        rider.setVerificationStatus(status);
        return userRepository.save(rider);
    }

    /**
     * Deactivate rider account.
     */
    @Transactional
    public void deactivateRider(UUID riderId) {
        Rider rider = userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        rider.setIsActive(false);
        userRepository.save(rider);
    }

    /**
     * Activate rider account.
     */
    @Transactional
    public void activateRider(UUID riderId) {
        Rider rider = userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        rider.setIsActive(true);
        userRepository.save(rider);
    }

    /**
     * Check if rider exists and is active.
     */
    public boolean isRiderActive(UUID riderId) {
        return userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .map(Rider::getIsActive)
                .orElse(false);
    }

    /**
     * Increment total rides for a rider.
     */
    @Transactional
    public void incrementTotalRides(UUID riderId) {
        Rider rider = userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        rider.setTotalRides(rider.getTotalRides() + 1);
        userRepository.save(rider);
    }

    /**
     * Add to total spent for a rider.
     */
    @Transactional
    public void addToTotalSpent(UUID riderId, Double amount) {
        Rider rider = userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        rider.setTotalSpent(rider.getTotalSpent() + amount);
        userRepository.save(rider);
    }

    /**
     * Update rider rating.
     */
    @Transactional
    public void updateRating(UUID riderId, Double newRating) {
        Rider rider = userRepository.findById(riderId)
                .filter(user -> user.getUserType() == UserType.RIDER)
                .map(user -> (Rider) user)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        rider.setRating(newRating);
        userRepository.save(rider);
    }

    /**
     * Map Rider entity to RiderProfileResponse DTO.
     */
    private RiderProfileResponse mapToRiderProfileResponse(Rider rider) {
        RiderProfileResponse response = new RiderProfileResponse();
        response.setId(rider.getId());
        response.setFirstName(rider.getFirstName());
        response.setLastName(rider.getLastName());
        response.setPhoneNumber(rider.getPhoneNumber());
        response.setEmail(rider.getEmail());
        response.setProfileImageUrl(rider.getProfileImageUrl());
        response.setUserType(rider.getUserType().name());
        response.setVerificationStatus(rider.getVerificationStatus().name());
        response.setCreatedAt(rider.getCreatedAt());
        response.setUpdatedAt(rider.getUpdatedAt());
        response.setPreferredPaymentMethod(rider.getPreferredPaymentMethod());
        response.setTotalRides(rider.getTotalRides());
        response.setTotalSpent(rider.getTotalSpent());
        response.setRating(rider.getRating());
        response.setIsActive(rider.getIsActive());
        return response;
    }
}