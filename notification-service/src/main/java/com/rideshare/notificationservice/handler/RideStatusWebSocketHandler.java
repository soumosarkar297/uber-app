package com.rideshare.notificationservice.handler;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Broadcasts ride status updates via WebSocket to connected riders and drivers.
 * Clients subscribe to /user/{userId}/queue/ride-updates for personalized updates.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RideStatusWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Sends a ride status update to both the rider and driver via WebSocket.
     */
    public void broadcastRideStatus(String rideId, String status,
                                     String riderId, String driverId,
                                     String message) {
        RideStatusPayload payload = new RideStatusPayload(
                rideId, status, message, LocalDateTime.now());

        if (riderId != null) {
            messagingTemplate.convertAndSendToUser(riderId, "/queue/ride-updates", payload);
        }
        if (driverId != null) {
            messagingTemplate.convertAndSendToUser(driverId, "/queue/ride-updates", payload);
        }

        // Also broadcast to ride-specific topic for any subscribers
        messagingTemplate.convertAndSend("/topic/rides/" + rideId + "/status", payload);

        log.debug("Ride status WebSocket broadcast: ride={} status={}", rideId, status);
    }

    /**
     * Sends a location update to a ride's tracking topic.
     */
    public void broadcastLocationUpdate(String rideId, String driverId,
                                         double latitude, double longitude) {
        String destination = "/topic/rides/" + rideId + "/location";
        LocationUpdatePayload payload = new LocationUpdatePayload(
                driverId, latitude, longitude, LocalDateTime.now());
        messagingTemplate.convertAndSend(destination, payload);
    }

    public record RideStatusPayload(
            String rideId,
            String status,
            String message,
            LocalDateTime timestamp
    ) {}

    public record LocationUpdatePayload(
            String driverId,
            double latitude,
            double longitude,
            LocalDateTime timestamp
    ) {}
}
