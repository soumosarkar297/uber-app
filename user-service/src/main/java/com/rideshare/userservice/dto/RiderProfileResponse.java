package com.rideshare.userservice.dto;

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

    private String preferredPaymentMethod;
    private Integer totalRides;
    private Double totalSpent;
    private Double rating;
    private Boolean isActive;
}