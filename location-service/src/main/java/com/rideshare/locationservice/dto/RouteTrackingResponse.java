package com.rideshare.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing route tracking summary for a ride.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing route tracking summary for a ride")
public class RouteTrackingResponse {

    @Schema(description = "Unique identifier of the ride", example = "RIDE-67890")
    private String rideId;

    @Schema(description = "Unique identifier of the driver", example = "DRV-12345")
    private String driverId;

    @Schema(description = "Total distance of the tracked route in kilometers", example = "12.5")
    private double totalDistanceKm;

    @Schema(description = "Average speed over the tracked route in km/h", example = "38.2")
    private double averageSpeedKmh;

    @Schema(description = "Total number of route points recorded", example = "120")
    private int pointCount;
}
