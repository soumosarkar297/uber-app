package com.rideshare.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for calculating estimated time of arrival.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for calculating estimated time of arrival")
public class EtaRequest {

    @Schema(description = "Unique identifier of the driver", example = "DRV-12345")
    private String driverId;

    @Schema(description = "Latitude coordinate of the pickup location", example = "28.6139")
    private double pickupLatitude;

    @Schema(description = "Longitude coordinate of the pickup location", example = "77.2090")
    private double pickupLongitude;

    @Schema(description = "Latitude coordinate of the drop-off location", example = "28.7041")
    private double dropLatitude;

    @Schema(description = "Longitude coordinate of the drop-off location", example = "77.1025")
    private double dropLongitude;
}
