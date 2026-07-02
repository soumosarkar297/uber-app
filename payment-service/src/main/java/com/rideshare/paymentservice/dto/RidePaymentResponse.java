package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload returned after processing a ride payment.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RidePaymentResponse {

    @Schema(description = "Unique identifier of the payment transaction", example = "txn-xyz-789")
    private String transactionId;

    @Schema(description = "Unique identifier of the ride", example = "ride-456")
    private String rideId;

    @Schema(description = "Total fare amount charged for the ride in INR", example = "350.00")
    private BigDecimal amount;

    @Schema(description = "Payment method used for the transaction", example = "WALLET")
    private String paymentMethod;

    @Schema(description = "Current status of the payment", example = "SUCCESS")
    private String status;

    @Schema(description = "Reference ID from the payment gateway", example = "pi_3N2x3y4z5a6b7c8d")
    private String gatewayRef;

    @Schema(description = "Rider's wallet balance after the payment was deducted", example = "2150.00")
    private BigDecimal walletBalanceAfter;

    @Schema(description = "Timestamp when the payment was processed")
    private LocalDateTime paidAt;
}
