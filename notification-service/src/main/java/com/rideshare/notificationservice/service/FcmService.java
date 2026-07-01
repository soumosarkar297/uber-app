package com.rideshare.notificationservice.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.rideshare.notificationservice.dto.PushNotificationPayload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends push notifications through Firebase Cloud Messaging.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final DeviceTokenService deviceTokenService;

    /**
     * Sends a push notification to a single device via FCM.
     */
    public boolean sendPushNotification(PushNotificationPayload payload) {
        List<String> tokens = deviceTokenService.getUserTokens(payload.getToken() != null
                ? "" : "");

        if (payload.getToken() != null && !payload.getToken().isEmpty()) {
            tokens = List.of(payload.getToken());
        }

        if (tokens.isEmpty()) {
            log.warn("No device tokens found for notification");
            return false;
        }

        try {
            for (String token : tokens) {
                Message message = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder()
                                .setTitle(payload.getTitle())
                                .setBody(payload.getBody())
                                .build())
                        .putAllData(Map.of(
                                "rideId", payload.getRideId() != null ? payload.getRideId() : "",
                                "clickAction", payload.getClickAction() != null ? payload.getClickAction() : ""))
                        .setAndroidConfig(com.google.firebase.messaging.AndroidConfig.builder()
                                .setNotification(AndroidNotification.builder()
                                        .setClickAction(payload.getClickAction())
                                        .build())
                                .build())
                        .build();

                FirebaseMessaging.getInstance().send(message);
            }

            log.info("Push notification sent: {}", payload.getTitle());
            return true;

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Sends a multicast push notification to multiple devices.
     */
    public int sendMulticastNotification(List<String> tokens, String title, String body,
                                          Map<String, String> data) {
        if (tokens.isEmpty()) return 0;

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data != null ? data : Map.of())
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Multicast notification sent: {} success, {} failure",
                    response.getSuccessCount(), response.getFailureCount());
            return response.getSuccessCount();

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast notification: {}", e.getMessage());
            return 0;
        }
    }
}
