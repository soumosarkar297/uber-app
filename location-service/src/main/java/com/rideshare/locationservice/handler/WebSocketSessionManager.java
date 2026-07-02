package com.rideshare.locationservice.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Tracks active WebSocket sessions and their associated users.
 * Provides session lookup for targeted messaging and connection health monitoring.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
public class WebSocketSessionManager {

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> rideSubscribers = new ConcurrentHashMap<>();

    /**
     * Registers a new WebSocket session.
     */
    public void registerSession(String sessionId, String userId, String role) {
        sessions.put(sessionId, new SessionInfo(userId, role, System.currentTimeMillis()));
        log.info("WebSocket session registered: {} for user: {} role: {}", sessionId, userId, role);
    }

    /**
     * Removes a WebSocket session on disconnect.
     */
    public void removeSession(String sessionId) {
        SessionInfo removed = sessions.remove(sessionId);
        if (removed != null) {
            log.info("WebSocket session removed: {} for user: {}", sessionId, removed.userId());
        }
        rideSubscribers.values().forEach(subs -> subs.remove(sessionId));
    }

    /**
     * Subscribes a session to a ride's location updates.
     */
    public void subscribeToRide(String sessionId, String rideId) {
        rideSubscribers.computeIfAbsent(rideId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    /**
     * Unsubscribes a session from a ride's location updates.
     */
    public void unsubscribeFromRide(String sessionId, String rideId) {
        Set<String> subs = rideSubscribers.get(rideId);
        if (subs != null) {
            subs.remove(sessionId);
            if (subs.isEmpty()) {
                rideSubscribers.remove(rideId);
            }
        }
    }

    /**
     * Gets all session IDs subscribed to a specific ride.
     */
    public Set<String> getRideSubscribers(String rideId) {
        return rideSubscribers.getOrDefault(rideId, Set.of());
    }

    /**
     * Checks if a session is still active.
     */
    public boolean isSessionActive(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    /**
     * Gets session info by session ID.
     */
    public SessionInfo getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Gets all active sessions for a given user.
     */
    public Set<String> getUserSessions(String userId) {
        return sessions.entrySet().stream()
                .filter(e -> e.getValue().userId().equals(userId))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Gets the total number of active sessions.
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }

    /**
     * Updates the last heartbeat time for a session.
     */
    public void heartbeat(String sessionId) {
        SessionInfo info = sessions.get(sessionId);
        if (info != null) {
            sessions.put(sessionId, new SessionInfo(
                    info.userId(), info.role(), System.currentTimeMillis()));
        }
    }

    /**
     * Finds stale sessions that haven't sent a heartbeat in the given timeout.
     */
    public Set<String> findStaleSessions(long timeoutMs) {
        long now = System.currentTimeMillis();
        return sessions.entrySet().stream()
                .filter(e -> (now - e.getValue().lastHeartbeat()) > timeoutMs)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    public record SessionInfo(String userId, String role, long lastHeartbeat) {
    }
}
