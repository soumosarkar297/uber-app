package com.rideshare.paymentservice.dto;

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
    private long amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private String paymentMethodId;
    private String customerId;
}
