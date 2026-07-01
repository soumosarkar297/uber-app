package com.rideshare.tripservice.dto;

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

    private String rideId;
    private String riderName;
    private String driverName;
    private String vehicleType;
    private String pickupAddress;
    private String dropAddress;
    private double distanceKm;
    private double durationMinutes;
    private BigDecimal fare;
    private BigDecimal surgeAmount;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime rideDate;
}
