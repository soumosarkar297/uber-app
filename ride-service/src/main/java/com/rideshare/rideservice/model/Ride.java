package com.rideshare.rideservice.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a ride in the system.
 * Maps to the 'rides' table in the database.
 * Tracks the complete lifecycle of a ride from request to completion.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "rides")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    /** Unique identifier of the ride (UUID) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Unique identifier of the rider who requested the ride */
    @Column(nullable = false)
    private String riderId;

    /** Unique identifier of the driver who accepted the ride (null until matched) */
    @Column(nullable = false)
    private String driverId;

    /** Latitude coordinate of the pickup location */
    @Column(nullable = false)
    private double pickupLatitude;

    /** Longitude coordinate of the pickup location */
    @Column(nullable = false)
    private double pickupLongitude;

    /** Human-readable address of the pickup location */
    @Column(nullable = false)
    private String pickupAddress;

    /** Latitude coordinate of the drop-off location */
    @Column(nullable = false)
    private double dropLatitude;

    /** Longitude coordinate of the drop-off location */
    @Column(nullable = false)
    private double dropLongitude;

    /** Human-readable address of the drop-off location */
    @Column(nullable = false)
    private String dropAddress;

    /** Current status of the ride lifecycle */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    /** Estimated fare calculated at ride creation */
    private double estimatedFare;

    /** Actual fare charged upon completion */
    private double actualFare;

    /** Timestamp when the ride was created (auto-set) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** Timestamp when the ride was last updated (auto-updated) */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** Timestamp when the ride started (driver began trip) */
    private LocalDateTime startedAt;

    /** Timestamp when the ride was completed */
    private LocalDateTime completedAt;
}
