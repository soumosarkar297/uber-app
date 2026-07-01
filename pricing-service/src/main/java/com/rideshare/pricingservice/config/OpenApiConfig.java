package com.rideshare.pricingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configures OpenAPI/Swagger documentation for the Pricing Service.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the OpenAPI specification with service metadata.
     */
    @Bean
    public OpenAPI pricingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pricing Service API")
                        .description("Dynamic Pricing, Surge Detection, Promo Codes, and Fare Estimation")
                        .version("1.0.0"));
    }
}
