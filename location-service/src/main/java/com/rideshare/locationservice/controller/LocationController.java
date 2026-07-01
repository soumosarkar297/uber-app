package com.rideshare.locationservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.locationservice.dto.DriverAvailabilityRequest;
import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.EtaRequest;
import com.rideshare.locationservice.dto.EtaResponse;
import com.rideshare.locationservice.dto.LocationHistoryEntry;
import com.rideshare.locationservice.dto.NearByDriverResponse;
import com.rideshare.locationservice.dto.RouteTrackingRequest;
import com.rideshare.locationservice.dto.RouteTrackingResponse;
import com.rideshare.locationservice.service.DriverAvailabilityService;
import com.rideshare.locationservice.service.LocationHistoryService;
import com.rideshare.locationservice.service.LocationService;
import com.rideshare.locationservice.service.RouteTrackingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing driver locations, nearby search, and route tracking.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/locations")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Location Tracking", description = "Driver location updates, nearby search, history, and WebSocket tracking")
public class LocationController {

    private final LocationService locationService;
    private final LocationHistoryService locationHistoryService;
    private final RouteTrackingService routeTrackingService;
    private final DriverAvailabilityService driverAvailabilityService;

    // ── Location Update APIs ──

    @PostMapping("/drivers/update")
    @Operation(summary = "Update Driver Location", description = "Updates a driver's live location (called every ~3s by driver app)")
    public ResponseEntity<String> updateDriverLocation(
            @Valid @RequestBody DriverLocationRequest request) {
        locationService.updateDriverLocation(request);
        return ResponseEntity.ok("Driver location updated");
    }

    @PostMapping("/drivers/{rideId}/update")
    @Operation(summary = "Update Driver Location for Ride", description = "Updates location and broadcasts to ride tracking WebSocket")
    public ResponseEntity<String> updateDriverLocationForRide(
            @PathVariable String rideId,
            @Valid @RequestBody DriverLocationRequest request) {
        locationService.updateDriverLocationForRide(rideId, request);
        return ResponseEntity.ok("Driver location updated for ride");
    }

    // ── Nearby Driver APIs ──

    @GetMapping("/drivers/nearby")
    @Operation(summary = "Find Nearby Drivers", description = "Finds drivers within a specified radius of a location")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(locationService.findNearByDrivers(latitude, longitude, radius));
    }

    @GetMapping("/drivers/nearby/available")
    @Operation(summary = "Find Nearby Available Drivers", description = "Finds only available drivers within radius")
    public ResponseEntity<List<NearByDriverResponse>> getNearbyAvailableDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(
                locationService.findNearbyAvailableDrivers(latitude, longitude, radius));
    }

    // ── Driver Location Queries ──

    @GetMapping("/drivers/{driverId}")
    @Operation(summary = "Get Driver Location", description = "Gets a driver's current location")
    public ResponseEntity<?> getDriverLocation(@PathVariable String driverId) {
        var point = locationService.getDriverLocation(driverId);
        if (point == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new DriverLocationRequest(driverId, point.getY(), point.getX(),
                null, null, null));
    }

    @GetMapping("/drivers/{driverId1}/distance/{driverId2}")
    @Operation(summary = "Distance Between Drivers", description = "Gets distance between two drivers in km")
    public ResponseEntity<Double> getDistanceBetweenDrivers(
            @PathVariable String driverId1,
            @PathVariable String driverId2) {
        Double distance = locationService.getDistanceBetweenDrivers(driverId1, driverId2);
        return ResponseEntity.ok(distance != null ? distance : 0.0);
    }

    // ── Location History APIs ──

    @GetMapping("/drivers/{driverId}/history")
    @Operation(summary = "Get Driver Location History", description = "Gets location history for a driver within time range")
    public ResponseEntity<List<LocationHistoryEntry>> getDriverHistory(
            @PathVariable String driverId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {
        return ResponseEntity.ok(locationHistoryService.getHistory(driverId, from, to));
    }

    @GetMapping("/drivers/{driverId}/history/latest")
    @Operation(summary = "Get Latest Location", description = "Gets the most recent location from history")
    public ResponseEntity<LocationHistoryEntry> getLatestLocation(
            @PathVariable String driverId) {
        LocationHistoryEntry entry = locationHistoryService.getLatestLocation(driverId);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(entry);
    }

    // ── Route Tracking APIs ──

    @PostMapping("/routes/{rideId}/track")
    @Operation(summary = "Record Route Points", description = "Records route tracking points for an active ride")
    public ResponseEntity<RouteTrackingResponse> recordRoute(
            @PathVariable String rideId,
            @RequestBody RouteTrackingRequest request) {
        routeTrackingService.recordRoute(rideId, request.getDriverId(),
                request.getRoutePoints());
        return ResponseEntity.ok(routeTrackingService.getRouteSummary(rideId,
                request.getDriverId()));
    }

    @GetMapping("/routes/{rideId}/summary")
    @Operation(summary = "Get Route Summary", description = "Gets route tracking summary for a ride")
    public ResponseEntity<RouteTrackingResponse> getRouteSummary(
            @PathVariable String rideId,
            @RequestParam String driverId) {
        return ResponseEntity.ok(routeTrackingService.getRouteSummary(rideId, driverId));
    }

    // ── ETA Calculation APIs ──

    @PostMapping("/eta")
    @Operation(summary = "Calculate ETA", description = "Calculates estimated time of arrival to pickup and trip duration")
    public ResponseEntity<EtaResponse> calculateEta(
            @RequestBody EtaRequest request) {
        var driverLocation = locationService.getDriverLocation(request.getDriverId());
        if (driverLocation == null) {
            return ResponseEntity.notFound().build();
        }

        double pickupDistance = haversine(
                driverLocation.getY(), driverLocation.getX(),
                request.getPickupLatitude(), request.getPickupLongitude());
        double tripDistance = haversine(
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDropLatitude(), request.getDropLongitude());

        // Assume average city speed of 30 km/h
        double avgSpeedKmh = 30.0;
        double pickupEta = (pickupDistance / avgSpeedKmh) * 60;
        double tripDuration = (tripDistance / avgSpeedKmh) * 60;

        EtaResponse response = new EtaResponse();
        response.setPickupEtaMinutes(Math.round(pickupEta * 10.0) / 10.0);
        response.setTripDurationMinutes(Math.round(tripDuration * 10.0) / 10.0);
        response.setTotalDistanceKm(
                Math.round((pickupDistance + tripDistance) * 100.0) / 100.0);
        response.setDriverLatitude(driverLocation.getY());
        response.setDriverLongitude(driverLocation.getX());

        return ResponseEntity.ok(response);
    }

    // ── Driver Availability APIs ──

    @PutMapping("/drivers/{driverId}/availability")
    @Operation(summary = "Set Driver Availability", description = "Sets whether a driver is available for rides")
    public ResponseEntity<String> setDriverAvailability(
            @PathVariable String driverId,
            @RequestBody DriverAvailabilityRequest request) {
        driverAvailabilityService.setAvailability(driverId, request.isAvailable(),
                request.getZone());
        return ResponseEntity.ok("Driver availability updated");
    }

    @GetMapping("/drivers/{driverId}/availability")
    @Operation(summary = "Check Driver Availability", description = "Checks if a driver is available")
    public ResponseEntity<Boolean> checkDriverAvailability(@PathVariable String driverId) {
        return ResponseEntity.ok(driverAvailabilityService.isAvailable(driverId));
    }

    @GetMapping("/drivers/available/count")
    @Operation(summary = "Count Available Drivers", description = "Gets count of available drivers in a zone")
    public ResponseEntity<Long> countAvailableDrivers(
            @RequestParam(required = false) String zone) {
        if (zone != null) {
            return ResponseEntity.ok(
                    driverAvailabilityService.getAvailableDriverCountInZone(zone));
        }
        return ResponseEntity.ok(
                (long) driverAvailabilityService.getAvailableDrivers().size());
    }

    // ── Area Queries ──

    @GetMapping("/drivers/area/count")
    @Operation(summary = "Count Drivers in Area", description = "Gets count of drivers in a circular area")
    public ResponseEntity<Long> countDriversInArea(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(
                locationService.getDriverCountInArea(latitude, longitude, radius));
    }

    @GetMapping("/drivers/rectangular")
    @Operation(summary = "Find Drivers in Rectangle", description = "Finds drivers within a rectangular area")
    public ResponseEntity<List<NearByDriverResponse>> findDriversInRectangle(
            @RequestParam double minLat,
            @RequestParam double maxLat,
            @RequestParam double minLon,
            @RequestParam double maxLon) {
        return ResponseEntity.ok(
                locationService.findDriversInRectangularArea(minLat, maxLat, minLon, maxLon));
    }

    // ── Remove Driver ──

    @DeleteMapping("/drivers/{driverId}")
    @Operation(summary = "Remove Driver", description = "Removes a driver from location tracking (goes offline)")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId) {
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed successfully");
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371 * c;
    }
}
