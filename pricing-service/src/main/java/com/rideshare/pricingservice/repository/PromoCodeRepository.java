package com.rideshare.pricingservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.pricingservice.entity.PromoCode;

/**
 * Provides data access for promo code entities.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PromoCodeRepository extends JpaRepository<PromoCode, String> {

    Optional<PromoCode> findByCodeAndActiveTrue(String code);
}
