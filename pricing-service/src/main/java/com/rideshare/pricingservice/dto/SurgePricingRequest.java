package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for calculating surge pricing in a zone.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgePricingRequest {

    @Schema(description = "Name of the zone to calculate surge pricing for", example = "koramangala")
    private String zone;

    @Schema(description = "Latitude coordinate of the zone center", example = "12.9352")
    private double latitude;

    @Schema(description = "Longitude coordinate of the zone center", example = "77.6245")
    private double longitude;

    @Schema(description = "Radius of the zone in kilometres", example = "5.0")
    private double radiusKm;
}
