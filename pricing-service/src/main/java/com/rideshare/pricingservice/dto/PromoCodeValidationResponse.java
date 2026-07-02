package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Whether the promo code is valid and applicable", example = "true")
    private boolean valid;

    @Schema(description = "The validated promo code", example = "WELCOME50")
    private String code;

    @Schema(description = "Description of the promo code offer", example = "Flat 50% off up to 100")
    private String description;

    @Schema(description = "Percentage discount offered by the promo", example = "50.00")
    private BigDecimal discountPercent;

    @Schema(description = "Maximum discount amount that can be applied", example = "100.00")
    private BigDecimal maxDiscount;

    @Schema(description = "Flat discount amount if the promo is not percentage-based", example = "75.00")
    private BigDecimal flatDiscount;

    @Schema(description = "Minimum order value required to use this promo", example = "200.00")
    private BigDecimal minOrderValue;

    @Schema(description = "Error message if the promo code is invalid", example = "Promo code has expired")
    private String errorMessage;
}
