package com.rideshare.driverservice.dto;

/**
 * DTO for driver profile response.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String profileImageUrl;
    private String verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
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

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
