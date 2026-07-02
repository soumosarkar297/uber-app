package com.rideshare.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for rider profile response.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RiderProfileResponse extends UserProfileResponse {

    @Schema(description = "Rider's preferred payment method", example = "CREDIT_CARD")
    private String preferredPaymentMethod;

    @Schema(description = "Total number of rides taken by the rider", example = "142")
    private Integer totalRides;

    @Schema(description = "Total amount spent by the rider in dollars", example = "2345.67")
    private Double totalSpent;

    @Schema(description = "Rider's average rating from drivers", example = "4.8")
    private Double rating;

    @Schema(description = "Whether the rider account is currently active", example = "true")
    private Boolean isActive;
}