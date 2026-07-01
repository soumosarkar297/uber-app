package com.rideshare.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating a new ride request.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

    @NotBlank(message = "Rider Id is required")
    private String riderId;

    @NotNull(message = "Pickup latitude is required")
    private double pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    private double pickupLongitude;

    @NotNull(message = "Pickup Address is required")
    private String pickupAddress;

    @NotNull(message = "Drop latitude is required")
    private double dropLatitude;

    @NotNull(message = "Drop longitude is required")
    private double dropLongitude;

    @NotNull(message = "Drop Address is required")
    private String dropAddress;

    /** Preferred vehicle type (SEDAN, SUV, HATCHBACK, LUXURY, BIKE, AUTO) */
    private String vehicleType;

    /** Promo code to apply */
    private String promoCode;
}
