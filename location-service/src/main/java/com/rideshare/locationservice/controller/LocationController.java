package com.rideshare.locationservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponse;
import com.rideshare.locationservice.service.LocationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for driver location management.
 * Provides endpoints for updating driver locations, finding nearby drivers,
 * and removing drivers when they go offline.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * Updates the location of a driver.
     * Called by the driver's app approximately every 3 seconds.
     *
     * @param driverLocationRequest the request containing driver ID, latitude, and longitude
     * @return ResponseEntity with success message
     * @throws IllegalArgumentException if the request is invalid
     */
    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(
            @RequestBody DriverLocationRequest driverLocationRequest
    ) {
        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok("Driver location updated");
    }

    /**
     * Finds nearby drivers within a specified radius.
     * Called by the Matching Service when a ride is requested.
     *
     * @param latitude  the latitude of the search center
     * @param longitude the longitude of the search center
     * @param radius    the search radius in kilometers (default: 5.0)
     * @return list of nearby drivers with their locations and distances, sorted by distance ascending
     * @throws IllegalArgumentException if coordinates are invalid
     */
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius
    ) {
        return ResponseEntity.ok(locationService.findNearByDrivers(latitude, longitude, radius));
    }

    /**
     * Removes a driver from the location tracking system.
     * Called when a driver goes offline.
     *
     * @param driverId the unique identifier of the driver to remove
     * @return ResponseEntity with success message
     * @throws IllegalArgumentException if driverId is null or empty
     */
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId) {
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed successfully");
    }

}
