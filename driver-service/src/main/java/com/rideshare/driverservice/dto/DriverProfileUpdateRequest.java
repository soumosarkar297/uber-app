package com.rideshare.driverservice.dto;

/**
 * DTO for driver profile update request.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileUpdateRequest {

    @Schema(description = "Updated first name of the driver", example = "Jane")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Schema(description = "Updated last name of the driver", example = "Smith")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Schema(description = "Updated email address of the driver", example = "jane.smith@example.com")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Schema(description = "Updated URL of the driver's profile image", example = "https://storage.example.com/profiles/jane_new.jpg")
    @Size(max = 255, message = "Profile image URL must not exceed 255 characters")
    private String profileImageUrl;

    @Schema(description = "Updated driver's license number", example = "DL-987654321")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @Schema(description = "Updated license expiry date in YYYY-MM-DD format", example = "2030-06-30")
    private String licenseExpiryDate;

    @Schema(description = "Updated vehicle registration number", example = "XYZ-5678")
    @Size(max = 20, message = "Vehicle number must not exceed 20 characters")
    private String vehicleNumber;

    @Schema(description = "Updated vehicle make and model", example = "Honda Accord")
    @Size(max = 50, message = "Vehicle model must not exceed 50 characters")
    private String vehicleModel;

    @Schema(description = "Updated color of the vehicle", example = "Black")
    @Size(max = 30, message = "Vehicle color must not exceed 30 characters")
    private String vehicleColor;

    @Schema(description = "Updated manufacturing year of the vehicle", example = "2023")
    private Integer vehicleYear;

    @Schema(description = "Updated type/category of the vehicle", example = "SUV")
    private String vehicleType;

    @Schema(description = "Whether the driver is currently available for rides", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Whether the driver is currently online", example = "false")
    private Boolean isOnline;
}
