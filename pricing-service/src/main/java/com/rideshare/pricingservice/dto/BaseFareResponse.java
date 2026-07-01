package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

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

    private String vehicleType;

    private String city;

    private BigDecimal baseFare;

    private BigDecimal perKmRate;

    private BigDecimal perMinuteRate;

    private BigDecimal minimumFare;

    private BigDecimal bookingFee;

    private BigDecimal cancellationFee;
}
