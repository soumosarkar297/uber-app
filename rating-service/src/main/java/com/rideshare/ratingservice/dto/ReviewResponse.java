package com.rideshare.ratingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO representing a review submitted by a user.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private String id;
    private String rideId;
    private String reviewerId;
    private String revieweeId;
    private String reviewerType;
    private int rating;
    private String comment;
    private String tags;
    private boolean anonymous;
    private LocalDateTime createdAt;
}
