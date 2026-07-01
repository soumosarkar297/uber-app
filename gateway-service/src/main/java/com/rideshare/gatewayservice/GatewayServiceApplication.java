package com.rideshare.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Gateway Service.
 * Acts as the single entry point for all rideshare microservices,
 * handling routing, Swagger aggregation, and CORS configuration.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
public class GatewayServiceApplication {

    /**
     * Starts the Gateway Service application.
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
