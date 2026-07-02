package com.rideshare.userservice.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for registration response.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    @Schema(description = "Unique identifier of the registered user", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Type of the registered user", example = "RIDER")
    private String userType;

    @Schema(description = "Verification status of the user account", example = "PENDING")
    private String verificationStatus;

    @Schema(description = "Response message describing the registration result", example = "Registration successful")
    private String message;
}