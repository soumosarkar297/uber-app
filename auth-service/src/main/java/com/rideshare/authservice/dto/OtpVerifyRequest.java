package com.rideshare.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(
        description = "Phone number in E.164 format (e.g., +1234567890)",
        example = "+14155552671",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format. Must be in E.164 format (e.g., +1234567890)")
    private String phoneNumber;

    @Schema(
        description = "6-digit one-time password sent to the phone number",
        example = "482916",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String otp;

    @Schema(
        description = "Unique identifier of the device requesting authentication",
        example = "device-abc-123",
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Device ID is required")
    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    private String deviceId;

    @Schema(
        description = "Additional device metadata (e.g., model, OS version, app version)",
        example = "{\"model\": \"iPhone 15\", \"os\": \"iOS 17.0\", \"appVersion\": \"2.1.0\"}"
    )
    private Map<String, Object> deviceInfo;
}
