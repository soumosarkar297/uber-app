package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload containing base fare configuration for a vehicle type.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseFareResponse {

    @Schema(description = "Type of vehicle", example = "SEDAN")
    private String vehicleType;

    @Schema(description = "City the pricing applies to", example = "Bengaluru")
    private String city;

    @Schema(description = "Fixed base fare for the vehicle type", example = "50.00")
    private BigDecimal baseFare;

    @Schema(description = "Rate charged per kilometre travelled", example = "12.00")
    private BigDecimal perKmRate;

    @Schema(description = "Rate charged per minute of travel", example = "3.00")
    private BigDecimal perMinuteRate;

    @Schema(description = "Minimum fare that will be charged", example = "100.00")
    private BigDecimal minimumFare;

    @Schema(description = "Fixed booking fee for every ride", example = "25.00")
    private BigDecimal bookingFee;

    @Schema(description = "Fee charged when a ride is cancelled", example = "150.00")
    private BigDecimal cancellationFee;
}
