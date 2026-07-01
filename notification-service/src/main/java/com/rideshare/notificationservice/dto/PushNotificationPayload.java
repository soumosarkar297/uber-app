package com.rideshare.notificationservice.dto;

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

    private String title;
    private String body;
    private String token;
    private String rideId;
    private String clickAction;
}
