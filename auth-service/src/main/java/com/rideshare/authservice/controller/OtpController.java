package com.rideshare.authservice.controller;

import com.rideshare.authservice.dto.ApiResponse;
import com.rideshare.authservice.dto.OtpSendRequest;
import com.rideshare.authservice.dto.OtpVerifyRequest;
import com.rideshare.authservice.dto.TokenResponse;
import com.rideshare.authservice.service.DeviceService;
import com.rideshare.authservice.service.JwtService;
import com.rideshare.authservice.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for OTP-based authentication operations.
 * Handles OTP sending and verification with token generation.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
@Slf4j
public class OtpController {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final DeviceService deviceService;

    /**
     * Sends an OTP to the specified phone number.
     * POST /api/auth/otp/send
     *
     * @param request the OTP send request containing phone number
     * @return success message
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        String phoneNumber = request.getPhoneNumber();
        log.info("Sending OTP to phone number: {}", phoneNumber);

        if (otpService.isRateLimited(phoneNumber)) {
            return ResponseEntity.status(429)
                    .body(ApiResponse.error("Too many OTP requests. Please try again later.", "RATE_LIMITED"));
        }

        otpService.incrementRateLimit(phoneNumber);
        String otp = otpService.generateAndStoreOtp(phoneNumber);

        // TODO: Send OTP via SMS provider (Twilio, etc.)
        log.debug("Generated OTP for {}: {}", phoneNumber, otp);

        return ResponseEntity.ok(ApiResponse.<Void>success("OTP sent successfully to " + phoneNumber, null));
    }

    /**
     * Verifies OTP and returns access and refresh tokens.
     * On success, registers the device and generates tokens.
     * POST /api/auth/otp/verify
     *
     * @param request the OTP verify request containing phone number, OTP, device ID, and device info
     * @return tokens response with access token, refresh token, and expiry
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<TokenResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String otp = request.getOtp();
        String deviceId = request.getDeviceId();
        Map<String, Object> deviceInfo = request.getDeviceInfo();

        log.info("Verifying OTP for phone number: {}, device: {}", phoneNumber, deviceId);

        try {
            boolean verified = otpService.verifyOtp(phoneNumber, otp);

            if (!verified) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Invalid or expired OTP", "INVALID_OTP"));
            }

            // Register device
            boolean deviceRegistered = deviceService.registerDevice(phoneNumber, deviceId, deviceInfo);
            if (!deviceRegistered) {
                log.warn("Device registration failed or device already exists: {} for {}", deviceId, phoneNumber);
            }

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(phoneNumber, "RIDER", deviceId);
            String refreshToken = jwtService.generateRefreshToken(phoneNumber, deviceId);

            TokenResponse tokenResponse = new TokenResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getAccessTokenExpirySeconds()
            );

            return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", tokenResponse));

        } catch (IllegalStateException e) {
            log.warn("OTP verification failed for {}: {}", phoneNumber, e.getMessage());
            return ResponseEntity.status(429)
                    .body(ApiResponse.error(e.getMessage(), "MAX_ATTEMPTS_EXCEEDED"));
        }
    }
}
