package com.rideshare.paymentservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.paymentservice.dto.AddMoneyRequest;
import com.rideshare.paymentservice.dto.CreateWalletRequest;
import com.rideshare.paymentservice.dto.RidePaymentRequest;
import com.rideshare.paymentservice.dto.RidePaymentResponse;
import com.rideshare.paymentservice.dto.TransactionResponse;
import com.rideshare.paymentservice.dto.WalletResponse;
import com.rideshare.paymentservice.service.RidePaymentService;
import com.rideshare.paymentservice.service.TransactionService;
import com.rideshare.paymentservice.service.WalletService;
import com.rideshare.paymentservice.service.WalletTopupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing wallet, payment, and transaction endpoints.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/payments")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Payment Service", description = "Wallet, payments, and transaction management")
public class PaymentController {

    private final WalletService walletService;
    private final WalletTopupService walletTopupService;
    private final RidePaymentService ridePaymentService;
    private final TransactionService transactionService;

    // ── Wallet APIs ──

    @PostMapping("/wallet/create")
    @Operation(summary = "Create Wallet", description = "Creates a new wallet for a user")
    public ResponseEntity<WalletResponse> createWallet(
            @Valid @RequestBody CreateWalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(walletService.createWallet(request));
    }

    @GetMapping("/wallet/{userId}")
    @Operation(summary = "Get Wallet", description = "Gets wallet details for a user")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @PostMapping("/wallet/{userId}/add-money")
    @Operation(summary = "Add Money", description = "Adds money to wallet via payment gateway")
    public ResponseEntity<TransactionResponse> addMoney(
            @PathVariable String userId,
            @Valid @RequestBody AddMoneyRequest request) {
        com.rideshare.paymentservice.entity.Transaction tx = walletTopupService.addMoneyToWallet(userId, request);
        TransactionResponse response = new TransactionResponse(
                tx.getId(), tx.getTransactionRef(), tx.getUserId(),
                tx.getType().name(), tx.getStatus().name(), tx.getAmount(),
                tx.getBalanceBefore(), tx.getBalanceAfter(), tx.getRideId(),
                tx.getGatewayRef(), tx.getPaymentMethod().name(),
                tx.getDescription(), tx.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    // ── Ride Payment APIs ──

    @PostMapping("/ride/pay")
    @Operation(summary = "Process Ride Payment", description = "Processes payment for a completed ride")
    public ResponseEntity<RidePaymentResponse> processRidePayment(
            @Valid @RequestBody RidePaymentRequest request) {
        return ResponseEntity.ok(ridePaymentService.processRidePayment(request));
    }

    // ── Transaction APIs ──

    @GetMapping("/transactions/{userId}")
    @Operation(summary = "Get Transaction History", description = "Gets paginated transaction history")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(userId, page, size));
    }

    @GetMapping("/transactions/ride/{rideId}")
    @Operation(summary = "Get Ride Transactions", description = "Gets all transactions for a ride")
    public ResponseEntity<List<TransactionResponse>> getRideTransactions(
            @PathVariable String rideId) {
        return ResponseEntity.ok(transactionService.getTransactionsByRide(rideId));
    }

    @GetMapping("/transactions/{userId}/type/{type}")
    @Operation(summary = "Get Transactions by Type", description = "Gets transactions filtered by type")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(
            @PathVariable String userId,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(userId, type, page, size));
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get Transaction", description = "Gets a single transaction by ID")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }
}
