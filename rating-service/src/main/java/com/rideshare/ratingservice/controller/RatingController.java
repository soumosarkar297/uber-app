package com.rideshare.ratingservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.ratingservice.dto.RatingAggregateResponse;
import com.rideshare.ratingservice.dto.ReviewResponse;
import com.rideshare.ratingservice.dto.SubmitReviewRequest;
import com.rideshare.ratingservice.service.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing ratings, reviews, and rating aggregation.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/ratings")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rating Service", description = "Rate riders/drivers, reviews, and rating aggregation")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/review")
    @Operation(summary = "Submit Review", description = "Submits a review for a rider or driver")
    public ResponseEntity<ReviewResponse> submitReview(
            @Valid @RequestBody SubmitReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ratingService.submitReview(request));
    }

    @GetMapping("/user/{userId}/aggregate")
    @Operation(summary = "Get Rating Aggregate", description = "Gets aggregated rating stats for a user")
    public ResponseEntity<RatingAggregateResponse> getRatingAggregate(
            @PathVariable String userId) {
        return ResponseEntity.ok(ratingService.getRatingAggregate(userId));
    }

    @GetMapping("/user/{userId}/reviews")
    @Operation(summary = "Get Reviews for User", description = "Gets all reviews received by a user")
    public ResponseEntity<List<ReviewResponse>> getReviewsForUser(
            @PathVariable String userId) {
        return ResponseEntity.ok(ratingService.getReviewsForUser(userId));
    }

    @GetMapping("/user/{userId}/reviews/given")
    @Operation(summary = "Get Reviews by User", description = "Gets all reviews given by a user")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(
            @PathVariable String userId) {
        return ResponseEntity.ok(ratingService.getReviewsByUser(userId));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get Ride Reviews", description = "Gets all reviews for a specific ride")
    public ResponseEntity<List<ReviewResponse>> getRideReviews(
            @PathVariable String rideId) {
        return ResponseEntity.ok(ratingService.getRideReviews(rideId));
    }
}
