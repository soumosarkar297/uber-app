package com.rideshare.driveronboardingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Unique identifier of the driver applying", example = "driver-98765", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @Schema(description = "Type of vehicle (e.g., sedan, SUV, luxury)", example = "sedan")
    private String vehicleType;

    @Schema(description = "Manufacturer of the vehicle", example = "Toyota")
    private String vehicleMake;

    @Schema(description = "Model name of the vehicle", example = "Camry")
    private String vehicleModel;

    @Schema(description = "Manufacturing year of the vehicle", example = "2022")
    private Integer vehicleYear;

    @Schema(description = "Color of the vehicle", example = "White")
    private String vehicleColor;

    @Schema(description = "Vehicle license plate number", example = "ABC-1234")
    private String licensePlateNumber;

    @Schema(description = "Driver's license number", example = "DL-987654321")
    private String licenseNumber;

    @Schema(description = "Expiry date of the driver's license", example = "2028-12-31")
    private String licenseExpiryDate;

    @Schema(description = "Name of the insurance provider", example = "State Farm")
    private String insuranceProvider;

    @Schema(description = "Insurance policy number", example = "POL-123456789")
    private String insurancePolicyNumber;

    @Schema(description = "Expiry date of the insurance policy", example = "2027-06-30")
    private String insuranceExpiryDate;
}
