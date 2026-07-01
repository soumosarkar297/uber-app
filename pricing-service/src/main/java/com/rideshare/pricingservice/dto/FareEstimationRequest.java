package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for estimating a complete ride fare.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimationRequest {

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotNull(message = "Pickup latitude is required")
    private double pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    private double pickupLongitude;

    @NotNull(message = "Drop latitude is required")
    private double dropLatitude;

    @NotNull(message = "Drop longitude is required")
    private double dropLongitude;

    /** Promo code to apply */
    private String promoCode;

    /** City for pricing rules */
    private String city;
}
