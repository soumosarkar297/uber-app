package com.rideshare.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(
        description = "Unique identifier of the device whose tokens should be revoked. If omitted, all tokens for the user are revoked.",
        example = "device-abc-123",
        maxLength = 100
    )
    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    private String deviceId;
}
