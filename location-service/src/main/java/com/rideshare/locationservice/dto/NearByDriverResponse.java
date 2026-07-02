package com.rideshare.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing details of a nearby driver.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing details of a nearby driver")
public class NearByDriverResponse {

    @Schema(description = "Unique identifier of the driver", example = "DRV-12345")
    private String driverId;

    @Schema(description = "Current latitude coordinate of the driver", example = "28.6139")
    private double latitude;

    @Schema(description = "Current longitude coordinate of the driver", example = "77.2090")
    private double longitude;

    @Schema(description = "Distance from the search center in kilometers", example = "2.5")
    private double distanceInKm;

    @Schema(description = "Driver's current heading in degrees (0-360)", example = "90.0")
    private Double heading;

    @Schema(description = "Driver's current speed in km/h", example = "35.0")
    private Double speed;
}
