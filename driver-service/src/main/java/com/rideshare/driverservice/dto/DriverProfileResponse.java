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

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileResponse {

    @Schema(description = "Unique identifier of the driver", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Driver's first name", example = "Jane")
    private String firstName;

    @Schema(description = "Driver's last name", example = "Smith")
    private String lastName;

    @Schema(description = "Driver's phone number in E.164 format", example = "+14155551234")
    private String phoneNumber;

    @Schema(description = "Driver's email address", example = "jane.smith@example.com")
    private String email;

    @Schema(description = "URL of the driver's profile image", example = "https://storage.example.com/profiles/jane.jpg")
    private String profileImageUrl;

    @Schema(description = "Verification status of the driver account", example = "VERIFIED")
    private String verificationStatus;

    @Schema(description = "Timestamp when the driver account was created", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the driver profile was last updated", example = "2025-06-20T14:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Driver's license number", example = "DL-123456789")
    private String licenseNumber;

    @Schema(description = "Driver's license expiry date", example = "2028-12-31")
    private LocalDate licenseExpiryDate;

    @Schema(description = "Vehicle registration number", example = "ABC-1234")
    private String vehicleNumber;

    @Schema(description = "Vehicle make and model", example = "Toyota Camry")
    private String vehicleModel;

    @Schema(description = "Color of the vehicle", example = "White")
    private String vehicleColor;

    @Schema(description = "Manufacturing year of the vehicle", example = "2022")
    private Integer vehicleYear;

    @Schema(description = "Type/category of the vehicle", example = "SEDAN")
    private String vehicleType;

    @Schema(description = "Whether the driver is currently available for rides", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Whether the driver is currently online", example = "true")
    private Boolean isOnline;

    @Schema(description = "Total number of trips completed by the driver", example = "523")
    private Integer totalTrips;

    @Schema(description = "Driver's average rating from riders", example = "4.9")
    private Double rating;

    @Schema(description = "Total earnings of the driver in dollars", example = "15234.50")
    private BigDecimal earnings;

    @Schema(description = "Driver's current latitude coordinate", example = "40.7128")
    private Double currentLatitude;

    @Schema(description = "Driver's current longitude coordinate", example = "-74.0060")
    private Double currentLongitude;

    @Schema(description = "Timestamp of the last location update", example = "2025-06-20T14:45:00")
    private LocalDateTime lastLocationUpdate;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
