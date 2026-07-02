package com.rideshare.matchingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rideshare.matchingservice.dto.DriverMetricsResponse;

import java.util.UUID;

/**
 * Feign client for communicating with the Driver Service.
 * Provides methods to query driver profile and metrics for matching.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@FeignClient(name = "driver-service", url = "${driver.service.url}")
public interface DriverServiceClient {

    /**
     * Retrieves driver profile and metrics by driver ID.
     *
     * @param driverId the UUID of the driver
     * @return driver metrics including rating and trip count
     */
    @GetMapping("/drivers/{id}")
    DriverMetricsResponse getDriverById(@PathVariable UUID id);
}
