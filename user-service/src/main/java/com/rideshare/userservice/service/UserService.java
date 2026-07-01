package com.rideshare.userservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.userservice.dto.ProfileUpdateRequest;
import com.rideshare.userservice.dto.UserProfileResponse;
import com.rideshare.userservice.entity.User;
import com.rideshare.userservice.entity.UserType;
import com.rideshare.userservice.entity.VerificationStatus;
import com.rideshare.userservice.event.UserProfileUpdatedEvent;
import com.rideshare.userservice.event.UserRegisteredEvent;
import com.rideshare.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Base service for common user operations.
 * Handles shared functionality for both riders and drivers.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;

    /**
     * Find user by phone number.
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * Find user by email.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by phone number and user type.
     */
    public Optional<User> findByPhoneNumberAndUserType(String phoneNumber, UserType userType) {
        return userRepository.findByPhoneNumberAndUserType(phoneNumber, userType);
    }

    /**
     * Find user by email and user type.
     */
    public Optional<User> findByEmailAndUserType(String email, UserType userType) {
        return userRepository.findByEmailAndUserType(email, userType);
    }

    /**
     * Find user by ID.
     */
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Check if phone number exists.
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    /**
     * Check if email exists.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Find users by user type.
     */
    public List<User> findByUserType(UserType userType) {
        return userRepository.findByUserType(userType);
    }

    /**
     * Find users by verification status.
     */
    public List<User> findByVerificationStatus(VerificationStatus verificationStatus) {
        return userRepository.findByVerificationStatus(verificationStatus);
    }

    /**
     * Get user profile response DTO.
     */
    public Optional<UserProfileResponse> getProfile(UUID userId) {
        return userRepository.findById(userId)
                .map(this::mapToUserProfileResponse);
    }

    /**
     * Update user verification status.
     */
    @Transactional
    public User updateVerificationStatus(UUID userId, VerificationStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setVerificationStatus(status);
        User updatedUser = userRepository.save(user);
        
        // Publish profile updated event with verification status change
        java.util.Map<String, Object> changes = java.util.Map.of("verificationStatus", status.name());
        UserProfileUpdatedEvent event = UserProfileUpdatedEvent.builder()
                .userId(userId)
                .changes(changes)
                .timestamp(java.time.Instant.now())
                .build();
        eventPublisher.publishUserProfileUpdated(event);
        
        return updatedUser;
    }

    /**
     * Update common profile fields.
     */
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Track changes for event
        java.util.Map<String, Object> changes = new java.util.HashMap<>();
        if (request.getFirstName() != null && !request.getFirstName().equals(user.getFirstName())) {
            changes.put("firstName", request.getFirstName());
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().equals(user.getLastName())) {
            changes.put("lastName", request.getLastName());
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            changes.put("email", request.getEmail());
            user.setEmail(request.getEmail());
        }
        if (request.getProfileImageUrl() != null && !request.getProfileImageUrl().equals(user.getProfileImageUrl())) {
            changes.put("profileImageUrl", request.getProfileImageUrl());
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        
        // Publish profile updated event if there were changes
        if (!changes.isEmpty()) {
            UserProfileUpdatedEvent event = UserProfileUpdatedEvent.builder()
                    .userId(userId)
                    .changes(changes)
                    .timestamp(java.time.Instant.now())
                    .build();
            eventPublisher.publishUserProfileUpdated(event);
        }
        
        return mapToUserProfileResponse(updatedUser);
    }

    /**
     * Count users by type.
     */
    public long countByUserType(UserType userType) {
        return userRepository.countByUserType(userType);
    }

    /**
     * Count users by verification status.
     */
    public long countByVerificationStatus(VerificationStatus verificationStatus) {
        return userRepository.countByVerificationStatus(verificationStatus);
    }

    /**
     * Map User entity to UserProfileResponse DTO.
     */
    private UserProfileResponse mapToUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setUserType(user.getUserType().name());
        response.setVerificationStatus(user.getVerificationStatus().name());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}