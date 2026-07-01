package com.rideshare.pricingservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.pricingservice.entity.PricingRule;

/**
 * Provides data access for pricing rule entities.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PricingRuleRepository extends JpaRepository<PricingRule, String> {

    Optional<PricingRule> findByVehicleTypeAndCityAndActiveTrue(String vehicleType, String city);

    Optional<PricingRule> findByVehicleTypeAndActiveTrue(String vehicleType);
}
