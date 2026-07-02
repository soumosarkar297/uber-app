package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating a Stripe payment intent.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentIntentRequest {

    @NotBlank(message = "Amount is required")
    @Schema(description = "Payment amount in the smallest currency unit (e.g., paise for INR)", example = "35000", requiredMode = Schema.RequiredMode.REQUIRED)
    private long amount;

    @NotBlank(message = "Currency is required")
    @Schema(description = "ISO 4217 currency code", example = "INR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "Stripe payment method ID (e.g., card token)", example = "pm_card_visa")
    private String paymentMethodId;

    @Schema(description = "Stripe customer ID associated with this payment", example = "cus_OxR5tJ8bQf1a2b3c")
    private String customerId;
}
