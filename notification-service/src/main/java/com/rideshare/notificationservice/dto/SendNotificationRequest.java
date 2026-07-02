package com.rideshare.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for sending a notification to a user.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    @Schema(description = "Unique identifier of the user to receive the notification", example = "user-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(description = "Title of the notification", example = "Ride Update", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Body text of the notification", example = "Your driver has arrived.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Body is required")
    private String body;

    @Schema(description = "Type of the notification (e.g., ride_update, promo, system)", example = "ride_update")
    private String type;

    @Schema(description = "Delivery channel for the notification (e.g., push, sms, email)", example = "push")
    private String channel;

    @Schema(description = "Associated ride identifier, if applicable", example = "ride-67890")
    private String rideId;

    @Schema(description = "Additional key-value data payload as a JSON string", example = "{\"orderId\": \"123\"}")
    private String data;
}
