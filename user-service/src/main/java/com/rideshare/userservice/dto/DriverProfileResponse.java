package com.rideshare.userservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for driver profile response.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileResponse extends UserProfileResponse {

    private String licenseNumber;
    private LocalDate licenseExpiryDate;
    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleColor;
    private Integer vehicleYear;
    private String vehicleType;
    private Boolean isAvailable;
    private Boolean isOnline;
    private Integer totalTrips;
    private Double rating;
    private BigDecimal earnings;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime lastLocationUpdate;
}