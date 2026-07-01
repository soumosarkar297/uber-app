package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.model.RideCancellationReason;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for cancelling an active ride.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideCancelRequest {

    @NotNull(message = "Cancellation reason is required")
    private RideCancellationReason reason;

    /** Who initiated the cancellation */
    private String cancelledBy;
}
