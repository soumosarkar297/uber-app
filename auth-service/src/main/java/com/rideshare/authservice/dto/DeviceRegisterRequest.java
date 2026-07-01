package com.rideshare.authservice.dto;

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

    @NotBlank(message = "Device ID is required")
    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    private String deviceId;

    private Map<String, Object> deviceInfo;
}
