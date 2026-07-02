package com.rideshare.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing surge pricing area information.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing surge pricing area information")
public class SurgeAreaResponse {

    @Schema(description = "Name of the surge pricing zone", example = "downtown")
    private String zone;

    @Schema(description = "Latitude coordinate of the center of the surge area", example = "28.6139")
    private double centerLatitude;

    @Schema(description = "Longitude coordinate of the center of the surge area", example = "77.2090")
    private double centerLongitude;

    @Schema(description = "Radius of the surge area in kilometers", example = "5.0")
    private double radiusKm;

    @Schema(description = "Number of available drivers in the zone", example = "42")
    private int driverCount;

    @Schema(description = "Number of ride requests in the zone", example = "65")
    private int activeRideCount;

    @Schema(description = "Surge pricing multiplier (1.0 = no surge, >1.0 = surge active)", example = "1.8")
    private double surgeMultiplier;
}
