package com.rideshare.paymentservice.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event published when a ride payment is successfully processed.
 * Consumed by notification-service and analytics pipeline.
 *
 * Topic: payment.completed
 * Key: rideId
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {

    private String rideId;
    private String riderId;
    private String driverId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private String status;
}
