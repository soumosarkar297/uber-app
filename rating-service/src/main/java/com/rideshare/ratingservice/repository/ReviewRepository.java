package com.rideshare.ratingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.ratingservice.entity.Review;

/**
 * Spring Data JPA repository for Review entities.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findByRideId(String rideId);

    List<Review> findByRevieweeIdOrderByCreatedAtDesc(String revieweeId);

    List<Review> findByReviewerIdOrderByCreatedAtDesc(String reviewerId);

    boolean existsByRideIdAndReviewerId(String rideId, String reviewerId);
}
