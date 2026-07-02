package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating a Razorpay payment order.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderRequest {

    @Schema(description = "Payment amount in the smallest currency unit (e.g., paise for INR)", example = "35000")
    private long amount;

    @Schema(description = "ISO 4217 currency code", example = "INR")
    private String currency;

    @Schema(description = "Unique receipt identifier for order tracking", example = "receipt-ride-456")
    private String receipt;
}
