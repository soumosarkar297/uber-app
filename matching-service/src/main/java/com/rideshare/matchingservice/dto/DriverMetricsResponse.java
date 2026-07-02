package com.rideshare.matchingservice.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for driver metrics used in matching algorithm.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverMetricsResponse {

    private UUID id;
    private Double rating;
    private Integer totalTrips;
    private Boolean isAvailable;
    private Boolean isOnline;
}
