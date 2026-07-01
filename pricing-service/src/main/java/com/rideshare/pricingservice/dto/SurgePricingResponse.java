package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload containing surge pricing details for a zone.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgePricingResponse {

    private String zone;

    private double surgeMultiplier;

    private int driverCount;

    private int activeRideCount;

    private BigDecimal surgeAmount;

    private String status;
}
