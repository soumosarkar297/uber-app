package com.rideshare.rideservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideStatus;

/**
 * Spring Data JPA repository for persisting and querying rides.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RideRepository extends JpaRepository<Ride, String> {

    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);

    List<Ride> findByDriverIdAndStatusIn(String driverId, List<RideStatus> statuses);
}
