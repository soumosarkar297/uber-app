package com.rideshare.pricingservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
 * Represents a pricing rule for a vehicle type in a specific city.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "pricing_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private BigDecimal baseFare;

    @Column(nullable = false)
    private BigDecimal perKmRate;

    @Column(nullable = false)
    private BigDecimal perMinuteRate;

    @Column(nullable = false)
    private BigDecimal minimumFare;

    private BigDecimal maximumFare;

    @Column(nullable = false)
    private BigDecimal bookingFee;

    @Column(nullable = false)
    private BigDecimal cancellationFee;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String city;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
