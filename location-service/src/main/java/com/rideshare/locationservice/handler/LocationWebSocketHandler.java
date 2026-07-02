package com.rideshare.locationservice.handler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rideshare.locationservice.dto.DriverLocationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles WebSocket broadcasting of driver/rider location updates and ride status events.
 * Integrates with {@link WebSocketSessionManager} for connection tracking and provides
 * broadcast frequency optimization via throttling.
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
    private final WebSocketSessionManager sessionManager;

    private static final long DRIVER_LOCATION_THROTTLE_MS = 3_000;
    private static final long RIDE_LOCATION_THROTTLE_MS = 2_000;
    private final Map<String, Long> driverLocationTimestamps = new ConcurrentHashMap<>();
    private final Map<String, Long> rideLocationTimestamps = new ConcurrentHashMap<>();

    /**
     * Broadcasts driver location update to all subscribers of /topic/drivers/{driverId}.
     * Throttled to avoid excessive broadcasts.
     */
    public void broadcastDriverLocation(DriverLocationRequest request) {
        long now = System.currentTimeMillis();
        Long lastBroadcast = driverLocationTimestamps.get(request.getDriverId());
        if (lastBroadcast != null && (now - lastBroadcast) < DRIVER_LOCATION_THROTTLE_MS) {
            return;
        }
        driverLocationTimestamps.put(request.getDriverId(), now);

        String destination = "/topic/drivers/" + request.getDriverId();
        messagingTemplate.convertAndSend(destination, request);
        log.debug("WebSocket broadcast for driver: {}", request.getDriverId());
    }

    /**
     * Broadcasts location to ride-specific topic for live tracking.
     * Throttled to 2-second intervals.
     */
    public void broadcastRideLocation(String rideId, DriverLocationRequest request) {
        long now = System.currentTimeMillis();
        Long lastBroadcast = rideLocationTimestamps.get(rideId);
        if (lastBroadcast != null && (now - lastBroadcast) < RIDE_LOCATION_THROTTLE_MS) {
            return;
        }
        rideLocationTimestamps.put(rideId, now);

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
     * Broadcasts rider location to the assigned driver for active ride tracking.
     * Sends to /topic/rides/{rideId}/rider-location so the driver can see the rider.
     */
    public void broadcastRiderLocation(String rideId, String riderId,
                                        double latitude, double longitude) {
        String destination = "/topic/rides/" + rideId + "/rider-location";
        messagingTemplate.convertAndSend(destination, new RiderLocationUpdate(
                riderId, latitude, longitude, LocalDateTime.now()));
        log.debug("Rider location broadcast for ride: {}", rideId);
    }

    /**
     * Broadcasts ride status changes to both rider and driver.
     * Sends to /topic/rides/{rideId}/status.
     */
    public void broadcastRideStatus(String rideId, String status,
                                     String riderId, String driverId) {
        String destination = "/topic/rides/" + rideId + "/status";
        RideStatusUpdate update = new RideStatusUpdate(rideId, status, LocalDateTime.now());
        messagingTemplate.convertAndSend(destination, update);

        if (riderId != null) {
            messagingTemplate.convertAndSendToUser(riderId, "/queue/ride-updates", update);
        }
        if (driverId != null) {
            messagingTemplate.convertAndSendToUser(driverId, "/queue/ride-updates", update);
        }
        log.debug("Ride status broadcast for ride: {} status: {}", rideId, status);
    }

    /**
     * Sends a direct message to a specific user session.
     */
    public void sendToUser(String sessionId, Object payload) {
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/location", payload);
    }

    /**
     * Cleans up stale broadcast timestamps (called periodically via @Scheduled).
     */
    @Scheduled(fixedRate = 300_000)
    public void cleanupStaleTimestamps() {
        long cutoff = System.currentTimeMillis() - 300_000;
        driverLocationTimestamps.entrySet().removeIf(e -> e.getValue() < cutoff);
        rideLocationTimestamps.entrySet().removeIf(e -> e.getValue() < cutoff);
    }

    public record RideLocationUpdate(
            String driverId,
            double latitude,
            double longitude,
            Double heading,
            LocalDateTime timestamp
    ) {}

    public record RiderLocationUpdate(
            String riderId,
            double latitude,
            double longitude,
            LocalDateTime timestamp
    ) {}

    public record RideStatusUpdate(
            String rideId,
            String status,
            LocalDateTime timestamp
    ) {}
}
