package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for processing a ride payment.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RidePaymentRequest {

    @NotBlank(message = "Ride ID is required")
    @Schema(description = "Unique identifier of the ride", example = "ride-456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rideId;

    @NotBlank(message = "Rider ID is required")
    @Schema(description = "Unique identifier of the rider paying for the ride", example = "user-123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String riderId;

    @NotBlank(message = "Driver ID is required")
    @Schema(description = "Unique identifier of the driver who completed the ride", example = "user-789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverId;

    @NotNull(message = "Amount is required")
    @Schema(description = "Total fare amount for the ride in INR", example = "350.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment method: WALLET, CREDIT_CARD, DEBIT_CARD, UPI, CASH", example = "WALLET", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentMethod;

    @Schema(description = "Payment gateway token for card or UPI payments (required for non-wallet methods)", example = "tok_1N2x3y4z5a6b7c8d")
    private String gatewayToken;
}
