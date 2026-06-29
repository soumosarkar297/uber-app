package com.rideshare.matchingservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rideshare.matchingservice.dto.NearByDriverResponse;

/**
 * Feign client for communicating with the Location Service.
 * Provides methods to query nearby drivers based on geographic coordinates.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@FeignClient(name = "location-service", url = "${location.service.url}")
public interface LocationServiceClient {

    /**
     * Retrieves a list of nearby drivers within the specified radius.
     *
     * @param latitude  the latitude coordinate of the search center
     * @param longitude the longitude coordinate of the search center
     * @param radius    the search radius in kilometers
     * @return a list of nearby driver responses containing driver details and distance
     */
    @GetMapping("/locations/drivers/nearby")
    List<NearByDriverResponse> getNearByDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius
    );

}
