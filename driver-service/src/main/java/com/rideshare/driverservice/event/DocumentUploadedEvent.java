package com.rideshare.driverservice.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when a document is uploaded.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadedEvent {

    private UUID documentId;
    private UUID driverId;
    private String documentType;
    private Instant timestamp;
}
