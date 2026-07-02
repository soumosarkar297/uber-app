package com.rideshare.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for registering a new device.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegisterRequest {

    @Schema(
        description = "Unique identifier of the device to register",
        example = "device-abc-123",
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Device ID is required")
    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    private String deviceId;

    @Schema(
        description = "Device metadata including model, OS, and app version",
        example = "{\"model\": \"Samsung Galaxy S24\", \"os\": \"Android 14\", \"appVersion\": \"2.1.0\"}"
    )
    private Map<String, Object> deviceInfo;
}
