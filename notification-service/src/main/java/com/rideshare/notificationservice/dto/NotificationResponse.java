package com.rideshare.notificationservice.dto;

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

    private String id;
    private String userId;
    private String type;
    private String channel;
    private String title;
    private String body;
    private String status;
    private String rideId;
    private java.time.LocalDateTime createdAt;
}
