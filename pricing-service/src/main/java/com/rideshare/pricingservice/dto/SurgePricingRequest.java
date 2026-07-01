package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

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

    private String zone;

    private double latitude;

    private double longitude;

    private double radiusKm;
}
