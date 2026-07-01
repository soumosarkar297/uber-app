package com.rideshare.pricingservice.dto;

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

    @NotBlank(message = "Promo code is required")
    private String promoCode;

    private String userId;

    private double orderValue;
}
