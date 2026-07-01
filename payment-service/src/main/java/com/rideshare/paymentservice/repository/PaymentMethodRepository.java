package com.rideshare.paymentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.paymentservice.entity.PaymentMethodEntity;

/**
 * Repository interface for PaymentMethodEntity persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, String> {

    List<PaymentMethodEntity> findByUserIdAndActiveTrue(String userId);

    Optional<PaymentMethodEntity> findByUserIdAndIsDefaultTrueAndActiveTrue(String userId);
}
