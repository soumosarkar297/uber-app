package com.rideshare.notificationservice.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.notificationservice.entity.Notification;

/**
 * Spring Data JPA repository for Notification entity operations.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, Notification.NotificationStatus status, Pageable pageable);

    List<Notification> findByRideId(String rideId);

    long countByUserIdAndStatus(String userId, Notification.NotificationStatus status);
}
