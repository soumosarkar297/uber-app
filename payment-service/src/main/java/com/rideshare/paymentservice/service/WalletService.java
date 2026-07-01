package com.rideshare.paymentservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.paymentservice.dto.CreateWalletRequest;
import com.rideshare.paymentservice.dto.WalletResponse;
import com.rideshare.paymentservice.entity.Wallet;
import com.rideshare.paymentservice.entity.Wallet.WalletUserType;
import com.rideshare.paymentservice.repository.WalletRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages wallet creation, retrieval, and balance operations for users.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * Creates a new wallet for a user or returns the existing active wallet.
     *
     * @param request the wallet creation request
     * @return the created or existing wallet response
     */
    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        var existing = walletRepository.findByUserIdAndActiveTrue(request.getUserId());
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(request.getUserId());
        wallet.setUserType(WalletUserType.valueOf(request.getUserType()));
        wallet.setBalance(java.math.BigDecimal.ZERO);
        wallet.setTotalAdded(java.math.BigDecimal.ZERO);
        wallet.setTotalSpent(java.math.BigDecimal.ZERO);
        wallet.setActive(true);

        Wallet saved = walletRepository.save(wallet);
        log.info("Wallet created for user: {}", request.getUserId());
        return mapToResponse(saved);
    }

    /**
     * Returns wallet details for the specified user.
     *
     * @param userId the user ID to look up
     * @return the wallet response
     */
    public WalletResponse getWallet(String userId) {
        Wallet wallet = walletRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
        return mapToResponse(wallet);
    }

    /**
     * Returns the raw wallet entity for the specified user.
     *
     * @param userId the user ID to look up
     * @return the wallet entity
     */
    public Wallet getWalletEntity(String userId) {
        return walletRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }

    /**
     * Returns an existing active wallet or creates a new one if none exists.
     *
     * @param userId the user ID
     * @param userType the user type (RIDER or DRIVER)
     * @return the wallet entity
     */
    public Wallet getOrCreateWallet(String userId, String userType) {
        return walletRepository.findByUserIdAndActiveTrue(userId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUserId(userId);
                    wallet.setUserType(WalletUserType.valueOf(userType));
                    wallet.setBalance(java.math.BigDecimal.ZERO);
                    wallet.setTotalAdded(java.math.BigDecimal.ZERO);
                    wallet.setTotalSpent(java.math.BigDecimal.ZERO);
                    wallet.setActive(true);
                    return walletRepository.save(wallet);
                });
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setUserId(wallet.getUserId());
        response.setUserType(wallet.getUserType().name());
        response.setBalance(wallet.getBalance());
        response.setTotalAdded(wallet.getTotalAdded());
        response.setTotalSpent(wallet.getTotalSpent());
        response.setActive(wallet.isActive());
        response.setCreatedAt(wallet.getCreatedAt());
        return response;
    }
}
