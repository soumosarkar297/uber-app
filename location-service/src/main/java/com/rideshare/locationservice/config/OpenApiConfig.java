package com.rideshare.locationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for the Location Service.
 * Defines API documentation for driver location tracking endpoints.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI locationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Location Service API")
                        .description("Driver Location Tracking and Nearby Driver Search")
                        .version("1.0.0"));
    }
}
