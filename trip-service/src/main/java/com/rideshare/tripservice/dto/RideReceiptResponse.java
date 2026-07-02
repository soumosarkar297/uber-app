package com.rideshare.tripservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a detailed ride receipt.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideReceiptResponse {

    @Schema(description = "Unique identifier of the ride", example = "ride-67890")
    private String rideId;

    @Schema(description = "Full name of the rider", example = "Jane Doe")
    private String riderName;

    @Schema(description = "Full name of the driver", example = "John Smith")
    private String driverName;

    @Schema(description = "Type of vehicle used for the ride", example = "sedan")
    private String vehicleType;

    @Schema(description = "Pickup location address", example = "123 Main St, New York, NY")
    private String pickupAddress;

    @Schema(description = "Dropoff location address", example = "456 Broadway, New York, NY")
    private String dropAddress;

    @Schema(description = "Trip distance in kilometers", example = "12.5")
    private double distanceKm;

    @Schema(description = "Trip duration in minutes", example = "25.0")
    private double durationMinutes;

    @Schema(description = "Base fare for the ride before additional charges", example = "18.00")
    private BigDecimal fare;

    @Schema(description = "Additional surge pricing amount", example = "6.00")
    private BigDecimal surgeAmount;

    @Schema(description = "Discount applied to the ride", example = "5.00")
    private BigDecimal discount;

    @Schema(description = "Final total amount charged to the rider", example = "19.00")
    private BigDecimal totalAmount;

    @Schema(description = "Payment method used for the ride", example = "credit_card")
    private String paymentMethod;

    @Schema(description = "Date and time when the ride took place")
    private LocalDateTime rideDate;
}
