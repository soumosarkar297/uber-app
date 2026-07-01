package com.rideshare.userservice.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when a user's profile is updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdatedEvent {

    private UUID userId;
    private Map<String, Object> changes;
    private Instant timestamp;
}
