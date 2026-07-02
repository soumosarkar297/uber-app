package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing a transaction record.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    @Schema(description = "Unique identifier of the transaction record", example = "txn-abc-001")
    private String id;

    @Schema(description = "Human-readable transaction reference number", example = "TXN-20260701-001")
    private String transactionRef;

    @Schema(description = "Unique identifier of the user who owns the wallet", example = "user-123")
    private String userId;

    @Schema(description = "Type of transaction (DEBIT, CREDIT, REFUND)", example = "DEBIT")
    private String type;

    @Schema(description = "Status of the transaction (SUCCESS, FAILED, PENDING)", example = "SUCCESS")
    private String status;

    @Schema(description = "Transaction amount in INR", example = "350.00")
    private BigDecimal amount;

    @Schema(description = "Wallet balance before this transaction", example = "2500.00")
    private BigDecimal balanceBefore;

    @Schema(description = "Wallet balance after this transaction", example = "2150.00")
    private BigDecimal balanceAfter;

    @Schema(description = "Associated ride ID if the transaction is ride-related", example = "ride-456")
    private String rideId;

    @Schema(description = "Reference ID from the payment gateway", example = "pi_3N2x3y4z5a6b7c8d")
    private String gatewayRef;

    @Schema(description = "Payment method used for the transaction", example = "WALLET")
    private String paymentMethod;

    @Schema(description = "Human-readable description of the transaction", example = "Payment for ride ride-456")
    private String description;

    @Schema(description = "Timestamp when the transaction was created")
    private LocalDateTime createdAt;
}
