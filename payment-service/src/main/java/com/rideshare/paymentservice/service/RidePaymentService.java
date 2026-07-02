package com.rideshare.paymentservice.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.paymentservice.dto.RidePaymentRequest;
import com.rideshare.paymentservice.dto.RidePaymentResponse;
import com.rideshare.paymentservice.entity.Transaction;
import com.rideshare.paymentservice.entity.Transaction.PaymentMethod;
import com.rideshare.paymentservice.entity.Transaction.TransactionStatus;
import com.rideshare.paymentservice.entity.Transaction.TransactionType;
import com.rideshare.paymentservice.entity.Wallet;
import com.rideshare.paymentservice.event.PaymentCompletedEvent;
import com.rideshare.paymentservice.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles payment processing for completed rides across multiple payment methods.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RidePaymentService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StripeService stripeService;
    private final RazorpayService razorpayService;

    private static final String PAYMENT_COMPLETED_TOPIC = "payment.completed";
    private static final String PAYMENT_FAILED_TOPIC = "payment.failed";

    /**
     * Processes a ride payment using the specified payment method.
     *
     * @param request the ride payment request details
     * @return payment response with transaction result
     */
    @Transactional
    public RidePaymentResponse processRidePayment(RidePaymentRequest request) {
        log.info("Processing payment for ride: {} amount: {}", request.getRideId(), request.getAmount());

        String transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentMethod method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());

        Transaction transaction = new Transaction();
        transaction.setTransactionRef(transactionRef);
        transaction.setUserId(request.getRiderId());
        transaction.setAmount(request.getAmount());
        transaction.setRideId(request.getRideId());
        transaction.setPaymentMethod(method);
        transaction.setType(TransactionType.RIDE_PAYMENT);
        transaction.setStatus(TransactionStatus.PENDING);

        try {
            switch (method) {
                case WALLET -> processWalletPayment(request, transaction);
                case STRIPE -> processStripePayment(request, transaction);
                case RAZORPAY -> processRazorpayPayment(request, transaction);
                case CASH -> processCashPayment(transaction);
                default -> throw new RuntimeException("Unsupported payment method: " + method);
            }

            transaction.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(transaction);

            PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
                    request.getRideId(),
                    request.getRiderId(),
                    request.getDriverId(),
                    request.getAmount(),
                    method.name(),
                    transaction.getTransactionRef(),
                    "COMPLETED");

            kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, request.getRideId(), completedEvent);

            return new RidePaymentResponse(
                    transaction.getId(),
                    request.getRideId(),
                    request.getAmount(),
                    method.name(),
                    "COMPLETED",
                    transaction.getGatewayRef(),
                    null,
                    java.time.LocalDateTime.now());

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setDescription("Payment failed: " + e.getMessage());
            transactionRepository.save(transaction);

            kafkaTemplate.send(PAYMENT_FAILED_TOPIC, request.getRideId(), transaction);
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    private void processWalletPayment(RidePaymentRequest request, Transaction transaction) {
        Wallet wallet = walletService.getOrCreateWallet(request.getRiderId(), "RIDER");

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        BigDecimal balanceBefore = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setTotalSpent(wallet.getTotalSpent().add(request.getAmount()));

        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setDescription("Ride payment via wallet");

        // Driver payout
        Wallet driverWallet = walletService.getOrCreateWallet(request.getDriverId(), "DRIVER");
        driverWallet.setBalance(driverWallet.getBalance().add(request.getAmount()));
        driverWallet.setTotalAdded(driverWallet.getTotalAdded().add(request.getAmount()));
    }

    private void processStripePayment(RidePaymentRequest request, Transaction transaction) {
        var result = stripeService.charge(request.getAmount(), "inr", request.getGatewayToken());
        transaction.setGatewayRef(result);
        transaction.setDescription("Ride payment via Stripe");
    }

    private void processRazorpayPayment(RidePaymentRequest request, Transaction transaction) {
        var result = razorpayService.capturePayment(request.getGatewayToken(), request.getAmount());
        transaction.setGatewayRef(result);
        transaction.setDescription("Ride payment via Razorpay");
    }

    private void processCashPayment(Transaction transaction) {
        transaction.setDescription("Ride payment via cash");
        transaction.setGatewayRef("CASH-" + UUID.randomUUID().toString().substring(0, 8));
    }
}
