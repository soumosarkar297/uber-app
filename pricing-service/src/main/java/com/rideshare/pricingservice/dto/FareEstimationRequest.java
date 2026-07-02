package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Type of vehicle for fare estimation", example = "SEDAN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @Schema(description = "Latitude coordinate of the pickup location", example = "12.9716", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Pickup latitude is required")
    private double pickupLatitude;

    @Schema(description = "Longitude coordinate of the pickup location", example = "77.5946", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Pickup longitude is required")
    private double pickupLongitude;

    @Schema(description = "Latitude coordinate of the drop location", example = "12.2958", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Drop latitude is required")
    private double dropLatitude;

    @Schema(description = "Longitude coordinate of the drop location", example = "76.6394", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Drop longitude is required")
    private double dropLongitude;

    @Schema(description = "Promo code to apply for a discount", example = "WELCOME50")
    private String promoCode;

    @Schema(description = "City used for zone-based pricing rules", example = "Bengaluru")
    private String city;
}
