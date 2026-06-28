package com.rideshare.locationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Location Service application. This service manages
 * driver location tracking using Redis GeoSpatial indexes. It provides REST
 * endpoints for updating driver locations and finding nearby drivers.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
public class LocationServiceApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(LocationServiceApplication.class, args);
    }

}
