package com.rideshare.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for registering a device for push notifications.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDeviceRequest {

    @Schema(description = "Unique identifier of the user owning the device", example = "user-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(description = "Firebase Cloud Messaging (FCM) registration token for the device", example = "dK1x2y3z...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "FCM token is required")
    private String token;

    @Schema(description = "Device platform (e.g., ios, android, web)", example = "android", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Platform is required")
    private String platform;
}
