package com.rideshare.ratingservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.ratingservice.dto.RatingAggregateResponse;
import com.rideshare.ratingservice.dto.ReviewResponse;
import com.rideshare.ratingservice.dto.SubmitReviewRequest;
import com.rideshare.ratingservice.entity.RatingAggregate;
import com.rideshare.ratingservice.entity.Review;
import com.rideshare.ratingservice.entity.Review.ReviewerType;
import com.rideshare.ratingservice.repository.RatingAggregateRepository;
import com.rideshare.ratingservice.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for managing reviews and rating aggregation.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final ReviewRepository reviewRepository;
    private final RatingAggregateRepository ratingAggregateRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String RATING_CACHE_PREFIX = "rating:aggregate:";

    /**
     * Submit a review for a ride and update the user's rating aggregate.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    @Transactional
    public ReviewResponse submitReview(SubmitReviewRequest request) {
        if (reviewRepository.existsByRideIdAndReviewerId(request.getRideId(),
                request.getReviewerId())) {
            throw new RuntimeException("You have already reviewed this ride");
        }

        Review review = new Review();
        review.setRideId(request.getRideId());
        review.setReviewerId(request.getReviewerId());
        review.setRevieweeId(request.getRevieweeId());
        review.setReviewerType(ReviewerType.valueOf(request.getReviewerType()));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setTags(request.getTags());
        review.setAnonymous(request.isAnonymous());

        Review saved = reviewRepository.save(review);

        // Update aggregate
        updateRatingAggregate(request.getRevieweeId(), request.getRating());

        log.info("Review submitted for ride: {} by: {}", request.getRideId(),
                request.getReviewerId());

        return mapToResponse(saved);
    }

    /**
     * Recalculate and persist the rating aggregate for a user.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    @Transactional
    public void updateRatingAggregate(String userId, int newRating) {
        RatingAggregate aggregate = ratingAggregateRepository.findByUserId(userId)
                .orElseGet(() -> {
                    RatingAggregate a = new RatingAggregate();
                    a.setUserId(userId);
                    return a;
                });

        // Calculate new average
        int totalBefore = aggregate.getTotalRatings();
        BigDecimal sumBefore = aggregate.getAverageRating().multiply(BigDecimal.valueOf(totalBefore));
        int totalAfter = totalBefore + 1;
        BigDecimal sumAfter = sumBefore.add(BigDecimal.valueOf(newRating));
        BigDecimal newAverage = sumAfter.divide(BigDecimal.valueOf(totalAfter), 2, RoundingMode.HALF_UP);

        aggregate.setAverageRating(newAverage);
        aggregate.setTotalRatings(totalAfter);

        // Update star counts
        switch (newRating) {
            case 1 -> aggregate.setOneStarCount(aggregate.getOneStarCount() + 1);
            case 2 -> aggregate.setTwoStarCount(aggregate.getTwoStarCount() + 1);
            case 3 -> aggregate.setThreeStarCount(aggregate.getThreeStarCount() + 1);
            case 4 -> aggregate.setFourStarCount(aggregate.getFourStarCount() + 1);
            case 5 -> aggregate.setFiveStarCount(aggregate.getFiveStarCount() + 1);
        }

        ratingAggregateRepository.save(aggregate);

        // Update cache
        redisTemplate.opsForValue().set(
                RATING_CACHE_PREFIX + userId,
                newAverage.toString(),
                java.time.Duration.ofHours(24));
    }

    /**
     * Retrieve the aggregated rating statistics for a user.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public RatingAggregateResponse getRatingAggregate(String userId) {
        // Try cache first
        String cached = redisTemplate.opsForValue().get(RATING_CACHE_PREFIX + userId);
        if (cached != null) {
            RatingAggregate aggregate = ratingAggregateRepository.findByUserId(userId)
                    .orElse(null);
            if (aggregate != null) {
                return mapToAggregateResponse(aggregate);
            }
        }

        RatingAggregate aggregate = ratingAggregateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No ratings found for user: " + userId));

        // Cache for 24 hours
        redisTemplate.opsForValue().set(
                RATING_CACHE_PREFIX + userId,
                aggregate.getAverageRating().toString(),
                java.time.Duration.ofHours(24));

        return mapToAggregateResponse(aggregate);
    }

    /**
     * Retrieve all reviews received by a user.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public List<ReviewResponse> getReviewsForUser(String userId) {
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Retrieve all reviews given by a user.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public List<ReviewResponse> getReviewsByUser(String userId) {
        return reviewRepository.findByReviewerIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Retrieve all reviews for a specific ride.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public List<ReviewResponse> getRideReviews(String rideId) {
        return reviewRepository.findByRideId(rideId)
                .stream().map(this::mapToResponse).toList();
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRideId(review.getRideId());
        response.setReviewerId(review.isAnonymous() ? "anonymous" : review.getReviewerId());
        response.setRevieweeId(review.getRevieweeId());
        response.setReviewerType(review.getReviewerType().name());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setTags(review.getTags());
        response.setAnonymous(review.isAnonymous());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    private RatingAggregateResponse mapToAggregateResponse(RatingAggregate aggregate) {
        RatingAggregateResponse response = new RatingAggregateResponse();
        response.setUserId(aggregate.getUserId());
        response.setAverageRating(aggregate.getAverageRating());
        response.setTotalRatings(aggregate.getTotalRatings());
        response.setFiveStarCount(aggregate.getFiveStarCount());
        response.setFourStarCount(aggregate.getFourStarCount());
        response.setThreeStarCount(aggregate.getThreeStarCount());
        response.setTwoStarCount(aggregate.getTwoStarCount());
        response.setOneStarCount(aggregate.getOneStarCount());
        return response;
    }
}
