package com.rideshare.notificationservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.notificationservice.dto.NotificationResponse;
import com.rideshare.notificationservice.dto.PushNotificationPayload;
import com.rideshare.notificationservice.entity.Notification;
import com.rideshare.notificationservice.entity.Notification.NotificationChannel;
import com.rideshare.notificationservice.entity.Notification.NotificationStatus;
import com.rideshare.notificationservice.entity.Notification.NotificationType;
import com.rideshare.notificationservice.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates notification creation, delivery, and persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;
    private final EmailService emailService;
    private final DeviceTokenService deviceTokenService;

    /**
     * Sends a notification through the specified channel.
     */
    public NotificationResponse sendNotification(String userId, String title, String body,
                                                  NotificationType type, NotificationChannel channel,
                                                  String rideId, String data) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setType(type);
        notification.setChannel(channel);
        notification.setRideId(rideId);
        notification.setData(data);
        notification.setStatus(NotificationStatus.PENDING);

        notificationRepository.save(notification);

        boolean sent = switch (channel) {
            case PUSH -> sendPushNotification(userId, title, body, rideId);
            case EMAIL -> sendEmailNotification(userId, title, body);
            case IN_APP -> true; // In-app notifications are just stored
            default -> false;
        };

        notification.setStatus(sent ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notification.setSentAt(sent ? LocalDateTime.now() : null);
        notificationRepository.save(notification);

        return mapToResponse(notification);
    }

    /**
     * Sends push notification to a user's devices.
     */
    private boolean sendPushNotification(String userId, String title, String body, String rideId) {
        List<String> tokens = deviceTokenService.getUserTokens(userId);
        if (tokens.isEmpty()) return false;

        PushNotificationPayload payload = new PushNotificationPayload();
        payload.setTitle(title);
        payload.setBody(body);
        payload.setToken(userId);
        payload.setRideId(rideId);
        payload.setClickAction("OPEN_RIDE");

        return fcmService.sendPushNotification(payload);
    }

    private boolean sendEmailNotification(String userId, String title, String body) {
        // In production, look up user email from user-service
        return emailService.sendEmail(userId + "@example.com", title, body);
    }

    /**
     * Listens for ride request events and notifies nearby drivers.
     */
    @KafkaListener(topics = "ride.requested", groupId = "notification-service-group")
    public void onRideRequested(com.rideshare.notificationservice.event.RideEvent event) {
        // Notify nearby drivers about new ride request
        sendNotification(event.getDriverId(),
                "New Ride Request",
                "A rider needs a ride from " + event.getPickupAddress(),
                NotificationType.RIDE_REQUESTED,
                NotificationChannel.PUSH,
                event.getRideId(), null);
    }

    /**
     * Listens for ride matched events and notifies the rider.
     */
    @KafkaListener(topics = "ride.matched", groupId = "notification-service-group")
    public void onRideMatched(com.rideshare.notificationservice.event.RideEvent event) {
        // Notify rider that driver accepted
        sendNotification(event.getRiderId(),
                "Driver Found!",
                "Your driver is on the way to pick you up",
                NotificationType.RIDE_ACCEPTED,
                NotificationChannel.PUSH,
                event.getRideId(), null);
    }

    /**
     * Listens for ride started events and notifies the rider.
     */
    @KafkaListener(topics = "ride.started", groupId = "notification-service-group")
    public void onRideStarted(com.rideshare.notificationservice.event.RideEvent event) {
        sendNotification(event.getRiderId(),
                "Ride Started",
                "Your trip has begun. Enjoy your ride!",
                NotificationType.RIDE_STARTED,
                NotificationChannel.PUSH,
                event.getRideId(), null);
    }

    /**
     * Listens for ride completed events and notifies the rider.
     */
    @KafkaListener(topics = "ride.completed", groupId = "notification-service-group")
    public void onRideCompleted(com.rideshare.notificationservice.event.RideEvent event) {
        sendNotification(event.getRiderId(),
                "Ride Completed",
                "Your ride is complete. Thank you!",
                NotificationType.RIDE_COMPLETED,
                NotificationChannel.PUSH,
                event.getRideId(), null);
    }

    /**
     * Listens for ride cancelled events and notifies the rider.
     */
    @KafkaListener(topics = "ride.cancelled", groupId = "notification-service-group")
    public void onRideCancelled(com.rideshare.notificationservice.event.RideEvent event) {
        sendNotification(event.getRiderId(),
                "Ride Cancelled",
                "Your ride has been cancelled",
                NotificationType.RIDE_CANCELLED,
                NotificationChannel.PUSH,
                event.getRideId(), null);
    }

    /**
     * Retrieves paginated notifications for a user.
     */
    public List<NotificationResponse> getUserNotifications(String userId, int page, int size) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Counts unread notifications for a user.
     */
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.SENT);
    }

    /**
     * Marks a notification as read.
     */
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setStatus(NotificationStatus.READ);
            notificationRepository.save(n);
        });
    }

    private NotificationResponse mapToResponse(Notification n) {
        NotificationResponse response = new NotificationResponse();
        response.setId(n.getId());
        response.setUserId(n.getUserId());
        response.setType(n.getType().name());
        response.setChannel(n.getChannel().name());
        response.setTitle(n.getTitle());
        response.setBody(n.getBody());
        response.setStatus(n.getStatus().name());
        response.setRideId(n.getRideId());
        response.setCreatedAt(n.getCreatedAt());
        return response;
    }
}
