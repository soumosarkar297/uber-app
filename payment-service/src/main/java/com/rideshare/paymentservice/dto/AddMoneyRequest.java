package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for adding money to a user wallet.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMoneyRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount to add to the wallet in INR", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Payment method used for adding money (CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING)", example = "CREDIT_CARD")
    private String paymentMethod;

    @Schema(description = "Payment gateway token returned by Stripe or Razorpay frontend SDK", example = "tok_1N2x3y4z5a6b7c8d")
    private String gatewayToken;
}
