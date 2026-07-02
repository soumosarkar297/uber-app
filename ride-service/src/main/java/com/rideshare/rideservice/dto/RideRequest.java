package com.rideshare.rideservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating a new ride request.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

    @Schema(description = "Unique identifier of the rider", example = "rider-abc-123")
    @NotBlank(message = "Rider Id is required")
    private String riderId;

    @Schema(description = "Latitude coordinate of the pickup location", example = "12.9716")
    @NotNull(message = "Pickup latitude is required")
    private double pickupLatitude;

    @Schema(description = "Longitude coordinate of the pickup location", example = "77.5946")
    @NotNull(message = "Pickup longitude is required")
    private double pickupLongitude;

    @Schema(description = "Human-readable address of the pickup location", example = "MG Road, Bengaluru")
    @NotNull(message = "Pickup Address is required")
    private String pickupAddress;

    @Schema(description = "Latitude coordinate of the drop location", example = "12.2958")
    @NotNull(message = "Drop latitude is required")
    private double dropLatitude;

    @Schema(description = "Longitude coordinate of the drop location", example = "76.6394")
    @NotNull(message = "Drop longitude is required")
    private double dropLongitude;

    @Schema(description = "Human-readable address of the drop location", example = "Mysuru Palace, Mysuru")
    @NotNull(message = "Drop Address is required")
    private String dropAddress;

    @Schema(description = "Preferred vehicle type", example = "SEDAN", allowableValues = {"SEDAN", "SUV", "HATCHBACK", "LUXURY", "BIKE", "AUTO"})
    private String vehicleType;

    @Schema(description = "Promo code to apply for a discount", example = "WELCOME50")
    private String promoCode;

    @Schema(description = "Preferred payment method", example = "WALLET", allowableValues = {"WALLET", "STRIPE", "RAZORPAY", "CASH"})
    private String paymentMethod;
}
