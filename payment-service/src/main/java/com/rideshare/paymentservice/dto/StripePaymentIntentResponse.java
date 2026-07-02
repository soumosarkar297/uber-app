package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Unique identifier of the Stripe payment intent", example = "pi_3N2x3y4z5a6b7c8d")
    private String paymentIntentId;

    @Schema(description = "Client secret used to confirm the payment intent on the frontend", example = "pi_3N2x3y4z5a6b7c8d_secret_abc123")
    private String clientSecret;

    @Schema(description = "Current status of the payment intent", example = "requires_confirmation")
    private String status;
}
