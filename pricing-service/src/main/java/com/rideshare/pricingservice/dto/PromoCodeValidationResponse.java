package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload containing the result of promo code validation.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeValidationResponse {

    private boolean valid;

    private String code;

    private String description;

    private BigDecimal discountPercent;

    private BigDecimal maxDiscount;

    private BigDecimal flatDiscount;

    private BigDecimal minOrderValue;

    private String errorMessage;
}
