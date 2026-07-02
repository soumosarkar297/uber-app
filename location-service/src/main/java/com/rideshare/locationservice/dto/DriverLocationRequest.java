package com.rideshare.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating a driver's live location.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for updating a driver's live location")
public class DriverLocationRequest {

    @NotBlank(message = "Driver ID is required")
    @Schema(description = "Unique identifier of the driver", example = "DRV-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverId;

    @NotNull(message = "Latitude is required")
    @Schema(description = "Current latitude coordinate of the driver", example = "28.6139", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latitude;

    @NotNull(message = "Longitude is required")
    @Schema(description = "Current longitude coordinate of the driver", example = "77.2090", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longitude;

    @Schema(description = "Optional heading in degrees (0-360) indicating direction of travel", example = "180.0")
    private Double heading;

    @Schema(description = "Optional speed in km/h", example = "45.5")
    private Double speed;

    @Schema(description = "Optional accuracy of the location in meters", example = "10.0")
    private Double accuracy;
}
