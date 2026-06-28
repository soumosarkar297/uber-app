package com.rideshare.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for ride request creation.
 * Contains all necessary information for a rider to request a new ride.
 * Validated using Jakarta Bean Validation annotations.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

    /**
     * Unique identifier of the rider requesting the ride.
     * Must not be blank.
     */
    @NotBlank(message = "Rider Id is required")
    private String riderId;

    /**
     * Latitude coordinate of the pickup location.
     * Valid range: -90 to 90 degrees. Must not be null.
     */
    @NotNull(message = "Pickup latitude is required")
    private double pickupLatitude;

    /**
     * Longitude coordinate of the pickup location.
     * Valid range: -180 to 180 degrees. Must not be null.
     */
    @NotNull(message = "Pickup longitude is required")
    private double pickupLongitude;

    /**
     * Human-readable address of the pickup location.
     * Must not be null.
     */
    @NotNull(message = "Pickup Address is required")
    private String pickupAddress;

    /**
     * Latitude coordinate of the drop-off location.
     * Valid range: -90 to 90 degrees. Must not be null.
     */
    @NotNull(message = "Drop latitude is required")
    private double dropLatitude;

    /**
     * Longitude coordinate of the drop-off location.
     * Valid range: -180 to 180 degrees. Must not be null.
     */
    @NotNull(message = "Drop longitude is required")
    private double dropLongitude;

    /**
     * Human-readable address of the drop-off location.
     * Must not be null.
     */
    @NotNull(message = "Drop Address is required")
    private String dropAddress;
}
