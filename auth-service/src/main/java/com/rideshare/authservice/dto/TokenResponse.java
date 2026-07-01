package com.rideshare.authservice.dto;

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

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType = "Bearer";

    public TokenResponse(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}
