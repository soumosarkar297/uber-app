package com.rideshare.authservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rideshare.authservice.dto.ApiResponse;
import com.rideshare.authservice.dto.TokenRefreshRequest;
import com.rideshare.authservice.dto.TokenResponse;
import com.rideshare.authservice.dto.TokenRevokeRequest;
import com.rideshare.authservice.service.DeviceService;
import com.rideshare.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for token management operations.
 * Handles token refresh, revocation, and JWKS endpoint.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/token")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final JwtService jwtService;
    private final DeviceService deviceService;

    /**
     * Refreshes access token using a valid refresh token.
     * Validates refresh token, checks device binding, blacklists old refresh token.
     * POST /api/auth/token/refresh
     *
     * @param request the token refresh request containing refresh token and device ID
     * @return new tokens response
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String deviceId = request.getDeviceId();

        log.info("Refreshing access token for device: {}", deviceId);

        try {
            // Validate refresh token
            var claims = jwtService.validateToken(refreshToken);

            // Check token type is refresh
            String tokenType = claims.get("tokenType", String.class);
            if (!"refresh".equals(tokenType)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Invalid token type. Expected refresh token", "INVALID_TOKEN_TYPE"));
            }

            String phoneNumber = claims.getSubject();
            String tokenDeviceId = claims.get("deviceId", String.class);

            // Validate device binding
            if (!deviceId.equals(tokenDeviceId)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Device ID mismatch", "DEVICE_MISMATCH"));
            }

            deviceService.validateDeviceBinding(phoneNumber, deviceId);

            // Blacklist old refresh token
            long refreshTokenTtl = jwtService.getRefreshTokenExpirySeconds();
            jwtService.blacklistToken(refreshToken, refreshTokenTtl);

            // Generate new tokens
            String newAccessToken = jwtService.generateAccessToken(phoneNumber, "RIDER", deviceId);
            String newRefreshToken = jwtService.generateRefreshToken(phoneNumber, deviceId);

            TokenResponse tokenResponse = new TokenResponse(
                    newAccessToken,
                    newRefreshToken,
                    jwtService.getAccessTokenExpirySeconds()
            );

            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", tokenResponse));

        } catch (Exception e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid or expired refresh token", "INVALID_REFRESH_TOKEN"));
        }
    }

    /**
     * Revokes (logs out) by blacklisting the access token.
     * Requires authentication via Bearer token.
     * POST /api/auth/token/revoke
     *
     * @param authentication the authenticated user principal
     * @param request the revoke request containing optional device ID
     * @param httpServletRequest the HTTP request to extract the access token
     * @return success message
     */
    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<Void>> revokeToken(
            Authentication authentication,
            @Valid @RequestBody(required = false) TokenRevokeRequest request,
            HttpServletRequest httpServletRequest) {

        String phoneNumber = authentication.getName();
        String deviceId = request != null ? request.getDeviceId() : null;

        log.info("Revoking tokens for user: {}, device: {}", phoneNumber, deviceId);

        // Extract access token from Authorization header
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            long accessTokenTtl = jwtService.getAccessTokenExpirySeconds();
            jwtService.blacklistToken(accessToken, accessTokenTtl);
        }

        if (deviceId != null) {
            // Revoke specific device
            deviceService.revokeDevice(phoneNumber, deviceId, jwtService);
        } else {
            // Revoke all devices - blacklist all refresh tokens would require
            // tracking tokens per device. For now, we rely on device validation
            // at request time to check registration status.
            log.info("Revoking all devices for user: {} (token blacklisting for all devices not fully implemented)", phoneNumber);
        }

        return ResponseEntity.ok(ApiResponse.<Void>success("Tokens revoked successfully", null));
    }

    /**
     * Returns the JWK Set for public key distribution.
     * GET /api/auth/token/jwks
     *
     * @return JWK Set JSON
     */
    @GetMapping("/jwks")
    public ResponseEntity<JsonNode> getJwks() {
        log.debug("Serving JWKS endpoint");
        JsonNode jwks = jwtService.getJwkSet();
        return ResponseEntity.ok(jwks);
    }
}
