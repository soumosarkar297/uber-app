package com.rideshare.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for verifying OTP and obtaining tokens.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format. Must be in E.164 format (e.g., +1234567890)")
    private String phoneNumber;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String otp;

    @NotBlank(message = "Device ID is required")
    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    private String deviceId;

    private Map<String, Object> deviceInfo;
}
