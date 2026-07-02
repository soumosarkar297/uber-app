package com.rideshare.userservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's phone number in E.164 format", example = "+14155552671")
    private String phoneNumber;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "URL of the user's profile image", example = "https://storage.example.com/profiles/john.jpg")
    private String profileImageUrl;

    @Schema(description = "Type of the user account", example = "RIDER")
    private String userType;

    @Schema(description = "Verification status of the user account", example = "VERIFIED")
    private String verificationStatus;

    @Schema(description = "Timestamp when the user account was created", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the user profile was last updated", example = "2025-06-20T14:45:00")
    private LocalDateTime updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}