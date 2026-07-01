package com.rideshare.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configures OpenAPI/Swagger documentation for the Payment Service.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the OpenAPI specification with service metadata.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("Wallet Management, Payment Processing, and Transaction History")
                        .version("1.0.0"));
    }
}
