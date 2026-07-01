package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

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

    private String vehicleType;

    private BigDecimal baseFare;

    private BigDecimal distanceFare;

    private BigDecimal timeFare;

    private BigDecimal bookingFee;

    private BigDecimal subtotalBeforeSurge;

    private BigDecimal surgeMultiplier;

    private BigDecimal surgeAmount;

    private BigDecimal subtotalAfterSurge;

    private BigDecimal discount;

    private BigDecimal totalFare;

    private double estimatedDistanceKm;

    private double estimatedDurationMinutes;

    private String promoCode;

    private String promoDescription;
}
