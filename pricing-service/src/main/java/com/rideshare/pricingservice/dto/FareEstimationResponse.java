package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload containing the complete fare breakdown for a ride.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimationResponse {

    @Schema(description = "Vehicle type used for the fare estimation", example = "SEDAN")
    private String vehicleType;

    @Schema(description = "Fixed base fare component", example = "50.00")
    private BigDecimal baseFare;

    @Schema(description = "Fare component based on distance travelled", example = "120.00")
    private BigDecimal distanceFare;

    @Schema(description = "Fare component based on travel duration", example = "80.00")
    private BigDecimal timeFare;

    @Schema(description = "Booking fee charged for the ride", example = "25.00")
    private BigDecimal bookingFee;

    @Schema(description = "Total fare before surge pricing is applied", example = "275.00")
    private BigDecimal subtotalBeforeSurge;

    @Schema(description = "Surge multiplier applied based on demand", example = "1.5")
    private BigDecimal surgeMultiplier;

    @Schema(description = "Additional amount charged due to surge pricing", example = "137.50")
    private BigDecimal surgeAmount;

    @Schema(description = "Total fare after surge pricing is applied", example = "412.50")
    private BigDecimal subtotalAfterSurge;

    @Schema(description = "Discount amount applied from promo code", example = "50.00")
    private BigDecimal discount;

    @Schema(description = "Final total fare to be charged", example = "362.50")
    private BigDecimal totalFare;

    @Schema(description = "Estimated distance of the ride in kilometres", example = "15.3")
    private double estimatedDistanceKm;

    @Schema(description = "Estimated duration of the ride in minutes", example = "25.5")
    private double estimatedDurationMinutes;

    @Schema(description = "Promo code applied, if any", example = "WELCOME50")
    private String promoCode;

    @Schema(description = "Description of the applied promo code", example = "Flat 50 off on first ride")
    private String promoDescription;
}
