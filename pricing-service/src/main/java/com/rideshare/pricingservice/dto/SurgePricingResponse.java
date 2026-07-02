package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Name of the zone", example = "koramangala")
    private String zone;

    @Schema(description = "Current surge multiplier for the zone", example = "1.5")
    private double surgeMultiplier;

    @Schema(description = "Number of available drivers in the zone", example = "12")
    private int driverCount;

    @Schema(description = "Number of active rides in the zone", example = "8")
    private int activeRideCount;

    @Schema(description = "Additional surge amount applied on top of base fare", example = "75.00")
    private BigDecimal surgeAmount;

    @Schema(description = "Current surge status for the zone", example = "HIGH_DEMAND")
    private String status;
}
