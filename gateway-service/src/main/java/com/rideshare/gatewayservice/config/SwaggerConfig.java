package com.rideshare.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

/**
 * Swagger configuration for the API Gateway.
 * Provides a unified OpenAPI specification for all rideshare microservices.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Custom OpenAPI configuration for the gateway.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rideshare API")
                        .description("Unified API documentation for all rideshare microservices")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Gateway")));
    }
}
