package com.rideshare.matchingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main entry point for the Matching Service application. This service handles
 * matching riders with nearby drivers when a ride is requested. Consumes
 * ride.requested events from Kafka and coordinates with Location Service.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
public class MatchingServiceApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(MatchingServiceApplication.class, args);
    }

}
