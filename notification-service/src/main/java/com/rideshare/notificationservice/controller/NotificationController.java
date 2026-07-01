package com.rideshare.notificationservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.notificationservice.dto.NotificationResponse;
import com.rideshare.notificationservice.dto.RegisterDeviceRequest;
import com.rideshare.notificationservice.dto.SendNotificationRequest;
import com.rideshare.notificationservice.entity.Notification.NotificationChannel;
import com.rideshare.notificationservice.entity.Notification.NotificationType;
import com.rideshare.notificationservice.service.DeviceTokenService;
import com.rideshare.notificationservice.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles HTTP requests for sending and managing notifications.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/notifications")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Notification Service", description = "Push notifications, email, and real-time updates")
public class NotificationController {

    private final NotificationService notificationService;
    private final DeviceTokenService deviceTokenService;

    @PostMapping("/send")
    @Operation(summary = "Send Notification", description = "Sends a notification to a user")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        NotificationType type = request.getType() != null
                ? NotificationType.valueOf(request.getType()) : NotificationType.SYSTEM;
        NotificationChannel channel = request.getChannel() != null
                ? NotificationChannel.valueOf(request.getChannel()) : NotificationChannel.PUSH;

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.sendNotification(
                        request.getUserId(), request.getTitle(), request.getBody(),
                        type, channel, request.getRideId(), request.getData()));
    }

    @PostMapping("/device/register")
    @Operation(summary = "Register Device", description = "Registers a device for push notifications")
    public ResponseEntity<String> registerDevice(
            @Valid @RequestBody RegisterDeviceRequest request) {
        deviceTokenService.registerDevice(request);
        return ResponseEntity.ok("Device registered successfully");
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get Notifications", description = "Gets paginated notification history")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, page, size));
    }

    @GetMapping("/{userId}/unread-count")
    @Operation(summary = "Get Unread Count", description = "Gets count of unread notifications")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark as Read", description = "Marks a notification as read")
    public ResponseEntity<String> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }
}
