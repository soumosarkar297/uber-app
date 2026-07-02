package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.model.RideCancellationReason;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Reason for cancelling the ride", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Cancellation reason is required")
    private RideCancellationReason reason;

    @Schema(description = "Identifier of the user who initiated the cancellation", example = "rider-abc-123")
    private String cancelledBy;
}
