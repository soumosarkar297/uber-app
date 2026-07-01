package com.rideshare.ratingservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity storing aggregated rating statistics for a user.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "rating_aggregates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    private int totalRatings = 0;

    @Column(nullable = false)
    private int fiveStarCount = 0;

    @Column(nullable = false)
    private int fourStarCount = 0;

    @Column(nullable = false)
    private int threeStarCount = 0;

    @Column(nullable = false)
    private int twoStarCount = 0;

    @Column(nullable = false)
    private int oneStarCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
