package com.rideshare.userservice.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing a driver in the system.
 * Extends the base User entity with driver-specific fields.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "drivers")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends User {

    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "license_expiry_date")
    private LocalDate licenseExpiryDate;

    @NotBlank(message = "Vehicle number is required")
    @Size(max = 20, message = "Vehicle number must not exceed 20 characters")
    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @NotBlank(message = "Vehicle model is required")
    @Size(max = 50, message = "Vehicle model must not exceed 50 characters")
    @Column(name = "vehicle_model", nullable = false, length = 50)
    private String vehicleModel;

    @NotBlank(message = "Vehicle color is required")
    @Size(max = 30, message = "Vehicle color must not exceed 30 characters")
    @Column(name = "vehicle_color", nullable = false, length = 30)
    private String vehicleColor;

    @Column(name = "vehicle_year")
    private Integer vehicleYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", length = 20)
    private VehicleType vehicleType = VehicleType.SEDAN;

    @Column(name = "is_available")
    private Boolean isAvailable = false;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "total_trips")
    private Integer totalTrips = 0;

    @Column(name = "rating", precision = 3, scale = 2)
    private Double rating = 5.0;

    @Column(name = "earnings", precision = 12, scale = 2)
    private BigDecimal earnings = BigDecimal.ZERO;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;
}
