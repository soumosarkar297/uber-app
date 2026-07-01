package com.rideshare.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating a driver's availability status.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverAvailabilityRequest {

    private String driverId;

    /** Whether the driver is available to accept rides */
    private boolean available;

    /** Driver's service zone (e.g., "downtown", "airport", "suburb") */
    private String zone;
}
