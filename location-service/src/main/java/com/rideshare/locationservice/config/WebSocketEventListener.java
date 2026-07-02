package com.rideshare.locationservice.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.rideshare.locationservice.handler.WebSocketSessionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listens for WebSocket lifecycle events and manages sessions via WebSocketSessionManager.
 * Tracks connection, disconnection, and subscription events for proper session management.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String userId = headers.getUser() != null ? headers.getUser().getName() : null;
        String role = headers.getFirstNativeHeader("role");
        sessionManager.registerSession(sessionId, userId, role);
        log.info("WebSocket connected: sessionId={} userId={} role={}", sessionId, userId, role);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        sessionManager.removeSession(sessionId);
        log.info("WebSocket disconnected: sessionId={}", sessionId);
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String destination = headers.getDestination();

        if (destination != null && destination.startsWith("/topic/rides/")) {
            String[] parts = destination.split("/");
            if (parts.length >= 4) {
                String rideId = parts[3];
                sessionManager.subscribeToRide(sessionId, rideId);
                log.debug("Session {} subscribed to ride {}", sessionId, rideId);
            }
        }
    }
}
