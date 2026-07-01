package com.rideshare.notificationservice.dto;

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

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "FCM token is required")
    private String token;

    @NotBlank(message = "Platform is required")
    private String platform;
}
