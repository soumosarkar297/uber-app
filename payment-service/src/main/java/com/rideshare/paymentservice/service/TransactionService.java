package com.rideshare.paymentservice.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.rideshare.paymentservice.dto.TransactionResponse;
import com.rideshare.paymentservice.entity.Transaction;
import com.rideshare.paymentservice.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides transaction querying and history retrieval capabilities.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Returns paginated transaction history for a user.
     *
     * @param userId the user ID
     * @param page the page number (zero-indexed)
     * @param size the page size
     * @return list of transaction responses
     */
    public List<TransactionResponse> getTransactionHistory(String userId, int page, int size) {
        return transactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns all transactions associated with a specific ride.
     *
     * @param rideId the ride ID
     * @return list of transaction responses
     */
    public List<TransactionResponse> getTransactionsByRide(String rideId) {
        return transactionRepository.findByRideId(rideId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns paginated transactions filtered by type for a user.
     *
     * @param userId the user ID
     * @param type the transaction type filter
     * @param page the page number (zero-indexed)
     * @param size the page size
     * @return list of transaction responses
     */
    public List<TransactionResponse> getTransactionsByType(String userId, String type, int page, int size) {
        return transactionRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(
                        userId,
                        Transaction.TransactionType.valueOf(type),
                        PageRequest.of(page, size))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns a single transaction by its ID.
     *
     * @param transactionId the transaction ID
     * @return the transaction response
     */
    public TransactionResponse getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToResponse(transaction);
    }

    private TransactionResponse mapToResponse(Transaction t) {
        TransactionResponse response = new TransactionResponse();
        response.setId(t.getId());
        response.setTransactionRef(t.getTransactionRef());
        response.setUserId(t.getUserId());
        response.setType(t.getType().name());
        response.setStatus(t.getStatus().name());
        response.setAmount(t.getAmount());
        response.setBalanceBefore(t.getBalanceBefore());
        response.setBalanceAfter(t.getBalanceAfter());
        response.setRideId(t.getRideId());
        response.setGatewayRef(t.getGatewayRef());
        response.setPaymentMethod(t.getPaymentMethod() != null ? t.getPaymentMethod().name() : null);
        response.setDescription(t.getDescription());
        response.setCreatedAt(t.getCreatedAt());
        return response;
    }
}
