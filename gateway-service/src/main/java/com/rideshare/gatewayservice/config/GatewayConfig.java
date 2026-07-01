package com.rideshare.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for API Gateway routing.
 * Defines route mappings for all microservices and Swagger proxy routes.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class GatewayConfig {

    /**
     * Custom route locator defining all API and Swagger proxy routes.
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ── Auth Service ──
                .route("auth-api", r -> r
                        .path("/api/v1/api/auth/**")
                        .uri("http://auth-service:8081"))

                // ── User Service ──
                .route("riders-api", r -> r
                        .path("/api/v1/api/riders/**")
                        .uri("http://user-service:8082"))

                .route("drivers-api", r -> r
                        .path("/api/v1/api/drivers/**")
                        .uri("http://driver-service:8086"))

                .route("documents-api", r -> r
                        .path("/api/v1/api/documents/**")
                        .uri("http://user-service:8082"))

                // ── Location Service ──
                .route("locations-api", r -> r
                        .path("/api/v1/locations/**")
                        .uri("http://location-service:8083"))

                // ── Ride Service ──
                .route("rides-api", r -> r
                        .path("/api/v1/rides/**")
                        .uri("http://ride-service:8084"))

                // ── Driver Service Swagger ──
                .route("driver-swagger", r -> r
                        .path("/driver/api-docs")
                        .filters(f -> f.rewritePath("/driver/api-docs", "/api/v1/api-docs"))
                        .uri("http://driver-service:8086"))

                .route("driver-swagger-ui", r -> r
                        .path("/driver/swagger-ui/**")
                        .filters(f -> f.rewritePath("/driver/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://driver-service:8086"))

                // ── Swagger proxy routes ──
                .route("auth-swagger", r -> r
                        .path("/auth/api-docs")
                        .filters(f -> f.rewritePath("/auth/api-docs", "/api/v1/api-docs"))
                        .uri("http://auth-service:8081"))

                .route("auth-swagger-ui", r -> r
                        .path("/auth/swagger-ui/**")
                        .filters(f -> f.rewritePath("/auth/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://auth-service:8081"))

                .route("user-swagger", r -> r
                        .path("/user/api-docs")
                        .filters(f -> f.rewritePath("/user/api-docs", "/api/v1/api-docs"))
                        .uri("http://user-service:8082"))

                .route("user-swagger-ui", r -> r
                        .path("/user/swagger-ui/**")
                        .filters(f -> f.rewritePath("/user/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://user-service:8082"))

                .route("location-swagger", r -> r
                        .path("/location/api-docs")
                        .filters(f -> f.rewritePath("/location/api-docs", "/api/v1/api-docs"))
                        .uri("http://location-service:8083"))

                .route("location-swagger-ui", r -> r
                        .path("/location/swagger-ui/**")
                        .filters(f -> f.rewritePath("/location/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://location-service:8083"))

                .route("ride-swagger", r -> r
                        .path("/ride/api-docs")
                        .filters(f -> f.rewritePath("/ride/api-docs", "/api/v1/api-docs"))
                        .uri("http://ride-service:8084"))

                .route("ride-swagger-ui", r -> r
                        .path("/ride/swagger-ui/**")
                        .filters(f -> f.rewritePath("/ride/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://ride-service:8084"))

                .build();
    }
}
