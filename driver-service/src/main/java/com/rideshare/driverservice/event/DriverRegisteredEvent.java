package com.rideshare.driverservice.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when a new driver is registered with vehicle information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegisteredEvent {

    private UUID userId;
    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleColor;
    private Integer vehicleYear;
    private String vehicleType;
    private String licenseNumber;
    private Instant timestamp;
}
