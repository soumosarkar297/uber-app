package com.rideshare.rideservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.rideservice.model.Ride;

/**
 * Spring Data JPA repository for Ride entity.
 * Provides standard CRUD operations and custom query methods.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RideRepository extends JpaRepository<Ride, String> {

    /**
     * Finds all rides for a specific rider, ordered by creation date descending.
     *
     * @param riderId the unique identifier of the rider
     * @return list of rides ordered by createdAt descending (most recent first)
     */
    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);

}
