package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload returned after creating a Razorpay payment order.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {

    @Schema(description = "Unique identifier of the Razorpay order", example = "order_9Q3x1aB2cD3eF4gH")
    private String orderId;

    @Schema(description = "Order amount in the smallest currency unit (e.g., paise for INR)", example = "35000")
    private String amount;

    @Schema(description = "ISO 4217 currency code", example = "INR")
    private String currency;
}
