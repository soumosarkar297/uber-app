package com.rideshare.locationservice.handler;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.rideshare.locationservice.dto.DriverLocationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles WebSocket broadcasting of driver location updates to subscribed clients.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LocationWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcasts driver location update to all subscribers of /topic/drivers/{driverId}
     */
    public void broadcastDriverLocation(DriverLocationRequest request) {
        String destination = "/topic/drivers/" + request.getDriverId();
        messagingTemplate.convertAndSend(destination, request);
        log.debug("WebSocket broadcast for driver: {}", request.getDriverId());
    }

    /**
     * Broadcasts location to ride-specific topic for live tracking
     * Subscribers of /topic/rides/{rideId}/location receive driver positions
     */
    public void broadcastRideLocation(String rideId, DriverLocationRequest request) {
        String destination = "/topic/rides/" + rideId + "/location";
        messagingTemplate.convertAndSend(destination, new RideLocationUpdate(
                request.getDriverId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getHeading(),
                LocalDateTime.now()
        ));
    }

    /**
     * Sends a direct message to a specific user session
     */
    public void sendToUser(String sessionId, Object payload) {
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/location", payload);
    }

    public record RideLocationUpdate(
            String driverId,
            double latitude,
            double longitude,
            Double heading,
            LocalDateTime timestamp
    ) {}
}
