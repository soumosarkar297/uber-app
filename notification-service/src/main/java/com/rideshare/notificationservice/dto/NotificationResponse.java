package com.rideshare.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing a notification.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    @Schema(description = "Unique identifier of the notification", example = "notif-abc123")
    private String id;

    @Schema(description = "ID of the user the notification was sent to", example = "user-12345")
    private String userId;

    @Schema(description = "Type of the notification", example = "ride_update")
    private String type;

    @Schema(description = "Delivery channel used", example = "push")
    private String channel;

    @Schema(description = "Title of the notification", example = "Ride Update")
    private String title;

    @Schema(description = "Body text of the notification", example = "Your driver has arrived.")
    private String body;

    @Schema(description = "Delivery status of the notification", example = "sent")
    private String status;

    @Schema(description = "Associated ride identifier, if applicable", example = "ride-67890")
    private String rideId;

    @Schema(description = "Timestamp when the notification was created")
    private java.time.LocalDateTime createdAt;
}
