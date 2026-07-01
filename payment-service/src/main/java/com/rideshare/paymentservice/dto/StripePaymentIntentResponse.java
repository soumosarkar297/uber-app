package com.rideshare.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload returned after creating a Stripe payment intent.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentIntentResponse {

    private String paymentIntentId;
    private String clientSecret;
    private String status;
}
