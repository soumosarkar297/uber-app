package com.rideshare.driveronboardingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new onboarding application.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    private String vehicleType;
    private String vehicleMake;
    private String vehicleModel;
    private Integer vehicleYear;
    private String vehicleColor;
    private String licensePlateNumber;
    private String licenseNumber;
    private String licenseExpiryDate;
    private String insuranceProvider;
    private String insurancePolicyNumber;
    private String insuranceExpiryDate;
}
