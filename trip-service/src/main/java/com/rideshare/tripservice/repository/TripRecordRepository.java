package com.rideshare.tripservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.tripservice.entity.TripRecord;

/**
 * Spring Data JPA repository for {@link TripRecord} persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TripRecordRepository extends JpaRepository<TripRecord, String> {

    Optional<TripRecord> findByRideId(String rideId);

    List<TripRecord> findByRiderIdOrderByCreatedAtDesc(String riderId, Pageable pageable);

    List<TripRecord> findByDriverIdOrderByCreatedAtDesc(String driverId, Pageable pageable);

    long countByRiderId(String riderId);

    long countByDriverId(String driverId);

    long countByRiderIdAndStatus(String riderId, String status);

    long countByDriverIdAndStatus(String driverId, String status);
}
