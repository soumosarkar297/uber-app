package com.rideshare.pricingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.pricingservice.entity.SurgeHistory;

/**
 * Provides data access for surge history entities.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SurgeHistoryRepository extends JpaRepository<SurgeHistory, String> {

    List<SurgeHistory> findByZoneOrderByRecordedAtDesc(String zone);
}
