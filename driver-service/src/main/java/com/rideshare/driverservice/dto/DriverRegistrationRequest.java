package com.rideshare.driverservice.dto;

/**
 * DTO for driver registration request.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 255, message = "Profile image URL must not exceed 255 characters")
    private String profileImageUrl;

    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "License expiry date must be in YYYY-MM-DD format")
    private String licenseExpiryDate;

    @NotBlank(message = "Vehicle number is required")
    @Size(max = 20, message = "Vehicle number must not exceed 20 characters")
    private String vehicleNumber;

    @NotBlank(message = "Vehicle model is required")
    @Size(max = 50, message = "Vehicle model must not exceed 50 characters")
    private String vehicleModel;

    @NotBlank(message = "Vehicle color is required")
    @Size(max = 30, message = "Vehicle color must not exceed 30 characters")
    private String vehicleColor;

    private Integer vehicleYear;

    private String vehicleType;

    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String licenseFileUrl;

    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String vehicleRegistrationFileUrl;

    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String insuranceFileUrl;
}
