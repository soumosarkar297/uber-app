package com.rideshare.driverservice.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when a document verification status is updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerifiedEvent {

    private UUID documentId;
    private UUID driverId;
    private String verificationStatus;
    private Instant timestamp;
}
