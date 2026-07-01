package com.rideshare.paymentservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a financial transaction record.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String transactionRef;

    @Column(nullable = false)
    private String walletId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    /** Ride ID if this is a ride payment */
    private String rideId;

    /** Payment gateway reference (Stripe/Razorpay charge ID) */
    private String gatewayRef;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TransactionType {
        WALLET_TOPUP, RIDE_PAYMENT, RIDE_REFUND, WITHDRAWAL, CASHBACK, ADJUSTMENT
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    public enum PaymentMethod {
        WALLET, CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, CASH, STRIPE, RAZORPAY
    }
}
