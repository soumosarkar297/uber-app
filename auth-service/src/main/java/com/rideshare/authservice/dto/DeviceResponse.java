package com.rideshare.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO containing device information.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {

    @Schema(
        description = "Unique identifier of the registered device",
        example = "device-abc-123"
    )
    private String deviceId;

    @Schema(
        description = "Device metadata including model, OS, and app version",
        example = "{\"model\": \"Samsung Galaxy S24\", \"os\": \"Android 14\", \"appVersion\": \"2.1.0\"}"
    )
    private Map<String, Object> deviceInfo;

    @Schema(
        description = "ISO-8601 timestamp of when the device was first registered",
        example = "2026-07-01T10:30:00Z"
    )
    private String registeredAt;

    @Schema(
        description = "ISO-8601 timestamp of when the device was last used for authentication",
        example = "2026-07-02T08:15:00Z"
    )
    private String lastUsedAt;
}
