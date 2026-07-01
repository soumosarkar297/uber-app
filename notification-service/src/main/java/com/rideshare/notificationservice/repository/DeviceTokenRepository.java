package com.rideshare.notificationservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.notificationservice.entity.DeviceToken;

/**
 * Spring Data JPA repository for DeviceToken entity operations.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, String> {

    List<DeviceToken> findByUserIdAndActiveTrue(String userId);

    Optional<DeviceToken> findByTokenAndActiveTrue(String token);
}
