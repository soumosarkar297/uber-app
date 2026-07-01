package com.rideshare.notificationservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a notification record.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    /** JSON string of additional data */
    @Column(columnDefinition = "TEXT")
    private String data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    private String rideId;

    private String errorMessage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    public enum NotificationType {
        RIDE_REQUESTED, RIDE_ACCEPTED, RIDE_DECLINED, RIDE_STARTED,
        RIDE_COMPLETED, RIDE_CANCELLED, DRIVER_ARRIVING,
        PAYMENT_RECEIVED, PAYMENT_FAILED,
        PROMOTIONAL, SYSTEM
    }

    public enum NotificationChannel {
        PUSH, EMAIL, SMS, IN_APP
    }

    public enum NotificationStatus {
        PENDING, SENT, DELIVERED, FAILED, READ
    }
}
