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

                // ── Pricing Service ──
                .route("pricing-api", r -> r
                        .path("/api/v1/pricing/**")
                        .uri("http://pricing-service:8087"))

                // ── Payment Service ──
                .route("payments-api", r -> r
                        .path("/api/v1/payments/**")
                        .uri("http://payment-service:8088"))

                // ── Notification Service ──
                .route("notifications-api", r -> r
                        .path("/api/v1/notifications/**")
                        .uri("http://notification-service:8089"))

                // ── Rating Service ──
                .route("ratings-api", r -> r
                        .path("/api/v1/ratings/**")
                        .uri("http://rating-service:8090"))

                // ── Trip Service ──
                .route("trips-api", r -> r
                        .path("/api/v1/trips/**")
                        .uri("http://trip-service:8091"))

                // ── Driver Onboarding Service ──
                .route("onboarding-api", r -> r
                        .path("/api/v1/onboarding/**")
                        .uri("http://driver-onboarding-service:8092"))

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

                // ── Pricing Service Swagger ──
                .route("pricing-swagger", r -> r
                        .path("/pricing/api-docs")
                        .filters(f -> f.rewritePath("/pricing/api-docs", "/api/v1/api-docs"))
                        .uri("http://pricing-service:8087"))

                .route("pricing-swagger-ui", r -> r
                        .path("/pricing/swagger-ui/**")
                        .filters(f -> f.rewritePath("/pricing/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://pricing-service:8087"))

                // ── Payment Service Swagger ──
                .route("payment-swagger", r -> r
                        .path("/payment/api-docs")
                        .filters(f -> f.rewritePath("/payment/api-docs", "/api/v1/api-docs"))
                        .uri("http://payment-service:8088"))

                .route("payment-swagger-ui", r -> r
                        .path("/payment/swagger-ui/**")
                        .filters(f -> f.rewritePath("/payment/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://payment-service:8088"))

                // ── Notification Service Swagger ──
                .route("notification-swagger", r -> r
                        .path("/notification/api-docs")
                        .filters(f -> f.rewritePath("/notification/api-docs", "/api/v1/api-docs"))
                        .uri("http://notification-service:8089"))

                .route("notification-swagger-ui", r -> r
                        .path("/notification/swagger-ui/**")
                        .filters(f -> f.rewritePath("/notification/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://notification-service:8089"))

                // ── Rating Service Swagger ──
                .route("rating-swagger", r -> r
                        .path("/rating/api-docs")
                        .filters(f -> f.rewritePath("/rating/api-docs", "/api/v1/api-docs"))
                        .uri("http://rating-service:8090"))

                .route("rating-swagger-ui", r -> r
                        .path("/rating/swagger-ui/**")
                        .filters(f -> f.rewritePath("/rating/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://rating-service:8090"))

                // ── Trip Service Swagger ──
                .route("trip-swagger", r -> r
                        .path("/trip/api-docs")
                        .filters(f -> f.rewritePath("/trip/api-docs", "/api/v1/api-docs"))
                        .uri("http://trip-service:8091"))

                .route("trip-swagger-ui", r -> r
                        .path("/trip/swagger-ui/**")
                        .filters(f -> f.rewritePath("/trip/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://trip-service:8091"))

                // ── Onboarding Service Swagger ──
                .route("onboarding-swagger", r -> r
                        .path("/onboarding/api-docs")
                        .filters(f -> f.rewritePath("/onboarding/api-docs", "/api/v1/api-docs"))
                        .uri("http://driver-onboarding-service:8092"))

                .route("onboarding-swagger-ui", r -> r
                        .path("/onboarding/swagger-ui/**")
                        .filters(f -> f.rewritePath("/onboarding/swagger-ui/(?<segment>.*)", "/api/v1/swagger-ui/${segment}"))
                        .uri("http://driver-onboarding-service:8092"))

                .build();
    }
}
