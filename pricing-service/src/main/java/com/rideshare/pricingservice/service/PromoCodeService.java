package com.rideshare.pricingservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.pricingservice.dto.ApplyPromoRequest;
import com.rideshare.pricingservice.dto.ApplyPromoResponse;
import com.rideshare.pricingservice.dto.PromoCodeValidationRequest;
import com.rideshare.pricingservice.dto.PromoCodeValidationResponse;
import com.rideshare.pricingservice.entity.PromoCode;
import com.rideshare.pricingservice.repository.PromoCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages promo code validation, application, and persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PROMO_USAGE_PREFIX = "promo:usage:";

    /**
     * Validates a promo code for a given user and order value.
     */
    public PromoCodeValidationResponse validatePromoCode(PromoCodeValidationRequest request) {
        var promo = promoCodeRepository.findByCodeAndActiveTrue(request.getPromoCode());

        if (promo.isEmpty()) {
            return new PromoCodeValidationResponse(
                    false, request.getPromoCode(), null, null, null, null, null,
                    "Invalid promo code");
        }

        PromoCode code = promo.get();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(code.getValidFrom()) || now.isAfter(code.getValidUntil())) {
            return new PromoCodeValidationResponse(
                    false, code.getCode(), null, null, null, null, null,
                    "Promo code has expired");
        }

        if (code.getCurrentUses() >= code.getMaxUsesPerUser()) {
            return new PromoCodeValidationResponse(
                    false, code.getCode(), null, null, null, null, null,
                    "Promo code usage limit reached");
        }

        if (!code.isActive()) {
            return new PromoCodeValidationResponse(
                    false, code.getCode(), null, null, null, null, null,
                    "Promo code is no longer available");
        }

        if (request.getOrderValue() > 0 && code.getMinOrderValue() != null
                && BigDecimal.valueOf(request.getOrderValue()).compareTo(code.getMinOrderValue()) < 0) {
            return new PromoCodeValidationResponse(
                    false, code.getCode(), null, null, null, null, code.getMinOrderValue(),
                    "Minimum order value of " + code.getMinOrderValue() + " required");
        }

        return new PromoCodeValidationResponse(
                true,
                code.getCode(),
                code.getDescription(),
                code.getDiscountPercent(),
                code.getMaxDiscount(),
                code.getFlatDiscount(),
                code.getMinOrderValue(),
                null);
    }

    /**
     * Applies a promo code to an order and returns the discounted amount.
     */
    @Transactional
    public ApplyPromoResponse applyPromoCode(ApplyPromoRequest request) {
        var validation = validatePromoCode(
                new PromoCodeValidationRequest(request.getPromoCode(), null,
                        request.getOrderValue().doubleValue()));

        if (!validation.isValid()) {
            return new ApplyPromoResponse(
                    false,
                    request.getOrderValue(),
                    BigDecimal.ZERO,
                    request.getOrderValue(),
                    validation.getErrorMessage());
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (validation.getDiscountPercent() != null && validation.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
            discount = request.getOrderValue()
                    .multiply(validation.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (validation.getMaxDiscount() != null) {
                discount = discount.min(validation.getMaxDiscount());
            }
        } else if (validation.getFlatDiscount() != null) {
            discount = validation.getFlatDiscount();
        }

        BigDecimal finalAmount = request.getOrderValue().subtract(discount).max(BigDecimal.ZERO);

        // Increment usage count
        var promo = promoCodeRepository.findByCodeAndActiveTrue(request.getPromoCode());
        if (promo.isPresent()) {
            PromoCode code = promo.get();
            code.setCurrentUses(code.getCurrentUses() + 1);
            code.setTotalUses(code.getTotalUses() + 1);
            promoCodeRepository.save(code);
        }

        return new ApplyPromoResponse(
                true,
                request.getOrderValue(),
                discount,
                finalAmount,
                validation.getDescription());
    }

    /**
     * Creates or updates a promo code.
     */
    public PromoCode createPromoCode(PromoCode promoCode) {
        return promoCodeRepository.save(promoCode);
    }

    /**
     * Gets a promo code by its code string.
     */
    public PromoCode getPromoCode(String code) {
        return promoCodeRepository.findByCodeAndActiveTrue(code).orElse(null);
    }
}
