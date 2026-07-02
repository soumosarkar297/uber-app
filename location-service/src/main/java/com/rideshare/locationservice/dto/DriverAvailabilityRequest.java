package com.rideshare.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating a driver's availability status.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for updating a driver's availability status")
public class DriverAvailabilityRequest {

    @Schema(description = "Unique identifier of the driver", example = "DRV-12345")
    private String driverId;

    @Schema(description = "Whether the driver is available to accept rides", example = "true")
    private boolean available;

    @Schema(description = "Driver's service zone (e.g., downtown, airport, suburb)", example = "downtown")
    private String zone;
}
