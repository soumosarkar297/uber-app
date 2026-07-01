package com.rideshare.userservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base DTO for user profile response.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String profileImageUrl;
    private String userType;
    private String verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}