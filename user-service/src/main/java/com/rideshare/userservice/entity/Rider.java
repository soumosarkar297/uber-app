package com.rideshare.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing a rider (passenger) in the system.
 * Extends the base User entity with rider-specific fields.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "riders")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Rider extends User {

    @Column(name = "preferred_payment_method", length = 50)
    private String preferredPaymentMethod;

    @Column(name = "total_rides")
    private Integer totalRides = 0;

    @Column(name = "total_spent", precision = 10, scale = 2)
    private Double totalSpent = 0.0;

    @Column(name = "rating", precision = 3, scale = 2)
    private Double rating = 5.0;

    @Column(name = "is_active")
    private Boolean isActive = true;
}