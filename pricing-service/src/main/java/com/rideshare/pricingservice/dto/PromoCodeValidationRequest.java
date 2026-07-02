package com.rideshare.pricingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for validating a promo code.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeValidationRequest {

    @Schema(description = "Promo code to validate", example = "WELCOME50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Promo code is required")
    private String promoCode;

    @Schema(description = "Unique identifier of the user applying the promo", example = "user-abc-123")
    private String userId;

    @Schema(description = "Total order value to check promo eligibility against", example = "500.00")
    private double orderValue;
}
