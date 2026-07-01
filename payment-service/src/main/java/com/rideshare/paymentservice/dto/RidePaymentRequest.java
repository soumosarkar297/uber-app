package com.rideshare.paymentservice.dto;

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
    private String rideId;

    @NotBlank(message = "Rider ID is required")
    private String riderId;

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    /** Payment method: WALLET, CREDIT_CARD, DEBIT_CARD, UPI, CASH */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    /** Gateway token for card/UPI payments */
    private String gatewayToken;
}
