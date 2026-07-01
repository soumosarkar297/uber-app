package com.rideshare.paymentservice.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.paymentservice.dto.AddMoneyRequest;
import com.rideshare.paymentservice.entity.Transaction;
import com.rideshare.paymentservice.entity.Transaction.PaymentMethod;
import com.rideshare.paymentservice.entity.Transaction.TransactionStatus;
import com.rideshare.paymentservice.entity.Transaction.TransactionType;
import com.rideshare.paymentservice.entity.Wallet;
import com.rideshare.paymentservice.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes wallet top-up transactions via external payment gateways.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WalletTopupService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final StripeService stripeService;
    private final RazorpayService razorpayService;

    /**
     * Adds money to a user's wallet by processing a payment through the configured gateway.
     *
     * @param userId the user whose wallet is topped up
     * @param request the top-up request with amount and payment details
     * @return the completed transaction record
     */
    @Transactional
    public Transaction addMoneyToWallet(String userId, AddMoneyRequest request) {
        Wallet wallet = walletService.getOrCreateWallet(userId, "RIDER");

        String transactionRef = "TOPUP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentMethod method = PaymentMethod.valueOf(
                request.getPaymentMethod() != null ? request.getPaymentMethod().toUpperCase() : "STRIPE");

        Transaction transaction = new Transaction();
        transaction.setTransactionRef(transactionRef);
        transaction.setWalletId(wallet.getId());
        transaction.setUserId(userId);
        transaction.setType(TransactionType.WALLET_TOPUP);
        transaction.setAmount(request.getAmount());
        transaction.setPaymentMethod(method);
        transaction.setStatus(TransactionStatus.PENDING);

        try {
            String gatewayRef = null;

            switch (method) {
                case STRIPE -> {
                    gatewayRef = stripeService.charge(request.getAmount(), "inr",
                            request.getGatewayToken());
                }
                case RAZORPAY -> {
                    gatewayRef = razorpayService.capturePayment(request.getGatewayToken(),
                            request.getAmount());
                }
            }

            BigDecimal balanceBefore = wallet.getBalance();
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
            wallet.setTotalAdded(wallet.getTotalAdded().add(request.getAmount()));

            transaction.setBalanceBefore(balanceBefore);
            transaction.setBalanceAfter(wallet.getBalance());
            transaction.setGatewayRef(gatewayRef);
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setDescription("Wallet topup via " + method.name());

            transactionRepository.save(transaction);
            log.info("Wallet topped up for user: {} amount: {}", userId, request.getAmount());

            return transaction;

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setDescription("Topup failed: " + e.getMessage());
            transactionRepository.save(transaction);
            throw new RuntimeException("Wallet topup failed: " + e.getMessage());
        }
    }
}
