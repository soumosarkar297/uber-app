package com.rideshare.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for sending a push notification via FCM.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationPayload {

    @Schema(description = "Title of the push notification", example = "Ride Update")
    private String title;

    @Schema(description = "Body text of the push notification", example = "Your driver is on the way.")
    private String body;

    @Schema(description = "FCM token of the target device", example = "dK1x2y3z...")
    private String token;

    @Schema(description = "Associated ride identifier, if applicable", example = "ride-67890")
    private String rideId;

    @Schema(description = "Deep-link action to execute when the notification is tapped", example = "OPEN_RIDE_DETAIL")
    private String clickAction;
}
