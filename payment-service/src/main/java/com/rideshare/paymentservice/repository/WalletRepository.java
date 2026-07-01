package com.rideshare.paymentservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.paymentservice.entity.Wallet;

/**
 * Repository interface for Wallet entity persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface WalletRepository extends JpaRepository<Wallet, String> {

    Optional<Wallet> findByUserIdAndActiveTrue(String userId);
}
