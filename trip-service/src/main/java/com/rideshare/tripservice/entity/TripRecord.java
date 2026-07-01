package com.rideshare.tripservice.entity;

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
 * JPA entity representing a persisted trip record.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "trip_records")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String rideId;

    @Column(nullable = false)
    private String riderId;

    private String driverId;

    private String driverName;

    private String riderName;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private double pickupLatitude;

    @Column(nullable = false)
    private double pickupLongitude;

    @Column(nullable = false)
    private String dropAddress;

    @Column(nullable = false)
    private double dropLatitude;

    @Column(nullable = false)
    private double dropLongitude;

    private double distanceKm;

    private double durationMinutes;

    private String status;

    private BigDecimal estimatedFare;

    private BigDecimal actualFare;

    private String paymentMethod;

    private double surgeMultiplier;

    private String vehicleType;

    private LocalDateTime requestedAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
