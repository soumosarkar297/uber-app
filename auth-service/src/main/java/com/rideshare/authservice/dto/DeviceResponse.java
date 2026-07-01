package com.rideshare.authservice.dto;

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

    private String deviceId;
    private Map<String, Object> deviceInfo;
    private String registeredAt;
    private String lastUsedAt;
}
