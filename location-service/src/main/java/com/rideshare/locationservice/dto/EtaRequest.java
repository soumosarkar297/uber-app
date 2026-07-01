package com.rideshare.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for calculating estimated time of arrival.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EtaRequest {

    private String driverId;

    private double pickupLatitude;

    private double pickupLongitude;

    private double dropLatitude;

    private double dropLongitude;
}
