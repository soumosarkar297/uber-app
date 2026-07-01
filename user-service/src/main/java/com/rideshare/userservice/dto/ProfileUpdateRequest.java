package com.rideshare.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for profile update request.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 255, message = "Profile image URL must not exceed 255 characters")
    private String profileImageUrl;

    @Size(max = 50, message = "Preferred payment method must not exceed 50 characters")
    private String preferredPaymentMethod;

    // Driver-specific fields
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    private String licenseExpiryDate;

    @Size(max = 20, message = "Vehicle number must not exceed 20 characters")
    private String vehicleNumber;

    @Size(max = 50, message = "Vehicle model must not exceed 50 characters")
    private String vehicleModel;

    @Size(max = 30, message = "Vehicle color must not exceed 30 characters")
    private String vehicleColor;

    private Integer vehicleYear;

    private String vehicleType;

    private Boolean isAvailable;

    private Boolean isOnline;
}