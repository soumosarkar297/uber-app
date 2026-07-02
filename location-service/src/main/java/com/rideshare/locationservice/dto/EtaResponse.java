package com.rideshare.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing estimated time of arrival calculations.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing estimated time of arrival calculations")
public class EtaResponse {

    @Schema(description = "Estimated time to pickup in minutes", example = "8.5")
    private double pickupEtaMinutes;

    @Schema(description = "Estimated trip duration in minutes", example = "25.0")
    private double tripDurationMinutes;

    @Schema(description = "Total estimated distance in kilometers", example = "15.3")
    private double totalDistanceKm;

    @Schema(description = "Current latitude coordinate of the driver", example = "28.6139")
    private double driverLatitude;

    @Schema(description = "Current longitude coordinate of the driver", example = "77.2090")
    private double driverLongitude;
}
