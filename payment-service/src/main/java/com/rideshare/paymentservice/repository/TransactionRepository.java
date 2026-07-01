package com.rideshare.paymentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.paymentservice.entity.Transaction;

/**
 * Repository interface for Transaction entity persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Optional<Transaction> findByTransactionRef(String transactionRef);

    List<Transaction> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<Transaction> findByRideId(String rideId);

    List<Transaction> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, Transaction.TransactionType type, Pageable pageable);
}
