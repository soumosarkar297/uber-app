package com.rideshare.locationservice.dto;

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
public class DriverLocationRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotNull(message = "Latitude is required")
    private double latitude;

    @NotNull(message = "Longitude is required")
    private double longitude;

    /** Optional heading in degrees (0-360) indicating direction of travel */
    private Double heading;

    /** Optional speed in km/h */
    private Double speed;

    /** Optional accuracy in meters */
    private Double accuracy;
}
