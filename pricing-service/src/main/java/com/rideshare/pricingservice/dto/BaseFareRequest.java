package com.rideshare.pricingservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for retrieving base fare configuration.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseFareRequest {

    @Schema(description = "Type of vehicle to look up base fare for", example = "SEDAN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @Schema(description = "City for which to retrieve base fare configuration", example = "Bengaluru", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "City is required")
    private String city;
}
