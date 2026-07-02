package com.rideshare.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing authentication tokens.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    @Schema(
        description = "JWT access token used to authenticate API requests",
        example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
        description = "Refresh token used to obtain a new access token without re-authentication",
        example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."
    )
    private String refreshToken;

    @Schema(
        description = "Time in seconds until the access token expires",
        example = "3600"
    )
    private long expiresIn;

    @Schema(
        description = "Type of the token, typically 'Bearer'",
        example = "Bearer",
        defaultValue = "Bearer"
    )
    private String tokenType = "Bearer";

    public TokenResponse(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}
