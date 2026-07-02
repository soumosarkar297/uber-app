package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for applying a promo code to an order.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyPromoRequest {

    @Schema(description = "Promo code to apply to the order", example = "WELCOME50")
    private String promoCode;

    @Schema(description = "Total order value before discount", example = "500.00")
    private BigDecimal orderValue;
}
