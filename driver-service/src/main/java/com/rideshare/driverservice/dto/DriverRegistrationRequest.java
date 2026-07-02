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

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationRequest {

    @Schema(description = "Driver's first name", example = "Jane", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Schema(description = "Driver's last name", example = "Smith", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Schema(description = "Driver's phone number in E.164 format", example = "+14155551234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Schema(description = "Driver's email address", example = "jane.smith@example.com")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Schema(description = "URL of the driver's profile image", example = "https://storage.example.com/profiles/jane.jpg")
    @Size(max = 255, message = "Profile image URL must not exceed 255 characters")
    private String profileImageUrl;

    @Schema(description = "Driver's license number", example = "DL-123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @Schema(description = "Driver's license expiry date in YYYY-MM-DD format", example = "2028-12-31")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "License expiry date must be in YYYY-MM-DD format")
    private String licenseExpiryDate;

    @Schema(description = "Vehicle registration number", example = "ABC-1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vehicle number is required")
    @Size(max = 20, message = "Vehicle number must not exceed 20 characters")
    private String vehicleNumber;

    @Schema(description = "Vehicle make and model", example = "Toyota Camry", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vehicle model is required")
    @Size(max = 50, message = "Vehicle model must not exceed 50 characters")
    private String vehicleModel;

    @Schema(description = "Color of the vehicle", example = "White", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vehicle color is required")
    @Size(max = 30, message = "Vehicle color must not exceed 30 characters")
    private String vehicleColor;

    @Schema(description = "Manufacturing year of the vehicle", example = "2022")
    private Integer vehicleYear;

    @Schema(description = "Type/category of the vehicle", example = "SEDAN")
    private String vehicleType;

    @Schema(description = "URL of the driver's license document", example = "https://storage.example.com/documents/license.pdf")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String licenseFileUrl;

    @Schema(description = "URL of the vehicle registration document", example = "https://storage.example.com/documents/registration.pdf")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String vehicleRegistrationFileUrl;

    @Schema(description = "URL of the vehicle insurance document", example = "https://storage.example.com/documents/insurance.pdf")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String insuranceFileUrl;
}
