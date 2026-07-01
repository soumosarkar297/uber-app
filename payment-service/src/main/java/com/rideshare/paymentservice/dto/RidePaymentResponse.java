package com.rideshare.paymentservice.dto;

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

    private String transactionId;
    private String rideId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String gatewayRef;
    private BigDecimal walletBalanceAfter;
    private LocalDateTime paidAt;
}
