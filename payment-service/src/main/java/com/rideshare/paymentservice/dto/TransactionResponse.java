package com.rideshare.paymentservice.dto;

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

    private String id;
    private String transactionRef;
    private String userId;
    private String type;
    private String status;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String rideId;
    private String gatewayRef;
    private String paymentMethod;
    private String description;
    private LocalDateTime createdAt;
}
