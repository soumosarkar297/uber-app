package com.rideshare.authservice.controller;

import com.rideshare.authservice.dto.ApiResponse;
import com.rideshare.authservice.dto.OtpSendRequest;
import com.rideshare.authservice.dto.OtpVerifyRequest;
import com.rideshare.authservice.dto.TokenResponse;
import com.rideshare.authservice.service.DeviceService;
import com.rideshare.authservice.service.JwtService;
import com.rideshare.authservice.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OTP Authentication", description = "OTP sending and verification endpoints")
public class OtpController {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final DeviceService deviceService;

    @PostMapping("/send")
    @Operation(summary = "Send OTP", description = "Sends a one-time password to the specified phone number")
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

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verifies OTP and returns access and refresh tokens")
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
