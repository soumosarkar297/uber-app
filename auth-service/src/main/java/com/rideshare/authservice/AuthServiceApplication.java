package com.rideshare.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Auth Service.
 * Handles phone OTP authentication, JWT token management, and device registration.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}