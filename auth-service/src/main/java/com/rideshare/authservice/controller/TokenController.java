package com.rideshare.authservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rideshare.authservice.dto.ApiResponse;
import com.rideshare.authservice.dto.TokenRefreshRequest;
import com.rideshare.authservice.dto.TokenResponse;
import com.rideshare.authservice.dto.TokenRevokeRequest;
import com.rideshare.authservice.service.DeviceService;
import com.rideshare.authservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/api/auth/token")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Token Management", description = "Token refresh, revocation, and JWKS endpoint")
public class TokenController {

    private final JwtService jwtService;
    private final DeviceService deviceService;

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refreshes access token using a valid refresh token")
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

    @PostMapping("/revoke")
    @Operation(summary = "Revoke Token", description = "Revokes (logs out) by blacklisting the access token")
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
            log.info("Revoking all devices for user: {} (token blacklisting for all devices not fully implemented)", phoneNumber);
        }

        return ResponseEntity.ok(ApiResponse.<Void>success("Tokens revoked successfully", null));
    }

    @GetMapping("/jwks")
    @Operation(summary = "Get JWKS", description = "Returns the JWK Set for public key distribution")
    public ResponseEntity<JsonNode> getJwks() {
        log.debug("Serving JWKS endpoint");
        JsonNode jwks = jwtService.getJwkSet();
        return ResponseEntity.ok(jwks);
    }
}
