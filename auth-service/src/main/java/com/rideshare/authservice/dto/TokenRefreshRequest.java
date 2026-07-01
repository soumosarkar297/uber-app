package com.rideshare.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for refreshing access token using refresh token.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    @NotBlank(message = "Device ID is required")
    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    private String deviceId;
}
