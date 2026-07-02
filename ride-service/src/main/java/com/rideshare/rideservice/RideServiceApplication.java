package com.rideshare.rideservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Ride Service application.
 * This service manages the ride lifecycle from request to completion.
 * It handles ride creation, status transitions, and ride history.
 * Publishes ride events to Kafka for matching and notification services.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class RideServiceApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RideServiceApplication.class, args);
    }

}
