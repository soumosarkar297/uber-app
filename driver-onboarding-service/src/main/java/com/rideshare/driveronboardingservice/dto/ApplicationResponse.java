package com.rideshare.driveronboardingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for onboarding application details.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    @Schema(description = "Unique identifier of the onboarding application", example = "app-abc123")
    private String id;

    @Schema(description = "ID of the driver who submitted the application", example = "driver-98765")
    private String driverId;

    @Schema(description = "Current status of the application", example = "pending_review")
    private String status;

    @Schema(description = "Type of vehicle", example = "sedan")
    private String vehicleType;

    @Schema(description = "Vehicle manufacturer", example = "Toyota")
    private String vehicleMake;

    @Schema(description = "Vehicle model name", example = "Camry")
    private String vehicleModel;

    @Schema(description = "Vehicle manufacturing year", example = "2022")
    private Integer vehicleYear;

    @Schema(description = "Vehicle color", example = "White")
    private String vehicleColor;

    @Schema(description = "Vehicle license plate number", example = "ABC-1234")
    private String licensePlateNumber;

    @Schema(description = "Driver's license number", example = "DL-987654321")
    private String licenseNumber;

    @Schema(description = "Current status of the background check", example = "in_progress")
    private String backgroundCheckStatus;

    @Schema(description = "Administrative notes on the application", example = "All documents verified")
    private String adminNotes;

    @Schema(description = "Timestamp when the application was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the application was last updated")
    private LocalDateTime updatedAt;
}
