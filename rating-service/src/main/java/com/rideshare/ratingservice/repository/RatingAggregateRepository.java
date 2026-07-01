package com.rideshare.ratingservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.ratingservice.entity.RatingAggregate;

/**
 * Spring Data JPA repository for RatingAggregate entities.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RatingAggregateRepository extends JpaRepository<RatingAggregate, String> {

    Optional<RatingAggregate> findByUserId(String userId);
}
