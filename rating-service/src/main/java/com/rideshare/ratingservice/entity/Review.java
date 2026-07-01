package com.rideshare.ratingservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a user review for a ride.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String rideId;

    @Column(nullable = false)
    private String reviewerId;

    @Column(nullable = false)
    private String revieweeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewerType reviewerType;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    /** Comma-separated tags like: CLEANliness,DRIVING,COMMUNICATION */
    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(nullable = false)
    private boolean anonymous = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum ReviewerType {
        RIDER, DRIVER
    }
}
