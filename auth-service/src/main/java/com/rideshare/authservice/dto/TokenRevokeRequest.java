package com.rideshare.authservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for revoking (logging out) tokens.
 * Device ID is optional - if not provided, revokes all tokens for the user.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRevokeRequest {

    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    private String deviceId;
}
