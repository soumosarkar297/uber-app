package com.rideshare.pricingservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores historical records of surge pricing events per zone.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "surge_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurgeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String zone;

    @Column(nullable = false)
    private BigDecimal surgeMultiplier;

    private int driverCount;

    private int activeRideCount;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
