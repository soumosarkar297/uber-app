package com.rideshare.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for updating device information.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUpdateRequest {

    @Schema(
        description = "Updated device metadata (e.g., new OS version, app version)",
        example = "{\"model\": \"Samsung Galaxy S24\", \"os\": \"Android 15\", \"appVersion\": \"2.2.0\"}"
    )
    private Map<String, Object> deviceInfo;
}
