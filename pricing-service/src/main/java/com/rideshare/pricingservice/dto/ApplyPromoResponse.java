package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload after applying a promo code to an order.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyPromoResponse {

    @Schema(description = "Whether the promo code was successfully applied", example = "true")
    private boolean applied;

    @Schema(description = "Original order amount before discount", example = "500.00")
    private BigDecimal originalAmount;

    @Schema(description = "Discount amount applied from the promo", example = "100.00")
    private BigDecimal discountAmount;

    @Schema(description = "Final amount after discount is applied", example = "400.00")
    private BigDecimal finalAmount;

    @Schema(description = "Description of the applied discount", example = "20% off applied")
    private String description;
}
