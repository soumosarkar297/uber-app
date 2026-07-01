package com.rideshare.tripservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.tripservice.dto.RideReceiptResponse;
import com.rideshare.tripservice.dto.TripAnalyticsResponse;
import com.rideshare.tripservice.dto.TripRecordResponse;
import com.rideshare.tripservice.service.TripService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for trip history, analytics, receipts, and data export.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/trips")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Trip Service", description = "Trip history, analytics, receipts, and export")
public class TripController {

    private final TripService tripService;

    @GetMapping("/rider/{riderId}")
    @Operation(summary = "Get Rider Trip History", description = "Gets paginated trip history for a rider")
    public ResponseEntity<List<TripRecordResponse>> getRiderTripHistory(
            @PathVariable String riderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tripService.getRiderTripHistory(riderId, page, size));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get Driver Trip History", description = "Gets paginated trip history for a driver")
    public ResponseEntity<List<TripRecordResponse>> getDriverTripHistory(
            @PathVariable String driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tripService.getDriverTripHistory(driverId, page, size));
    }

    @GetMapping("/rider/{riderId}/analytics")
    @Operation(summary = "Get Rider Analytics", description = "Gets trip analytics for a rider")
    public ResponseEntity<TripAnalyticsResponse> getRiderAnalytics(
            @PathVariable String riderId) {
        return ResponseEntity.ok(tripService.getRiderAnalytics(riderId));
    }

    @GetMapping("/driver/{driverId}/analytics")
    @Operation(summary = "Get Driver Analytics", description = "Gets trip analytics for a driver")
    public ResponseEntity<TripAnalyticsResponse> getDriverAnalytics(
            @PathVariable String driverId) {
        return ResponseEntity.ok(tripService.getDriverAnalytics(driverId));
    }

    @GetMapping("/receipt/{rideId}")
    @Operation(summary = "Get Ride Receipt", description = "Gets detailed receipt for a completed ride")
    public ResponseEntity<RideReceiptResponse> getRideReceipt(
            @PathVariable String rideId) {
        return ResponseEntity.ok(tripService.getRideReceipt(rideId));
    }

    @GetMapping("/export/{userId}")
    @Operation(summary = "Export Trip Data", description = "Exports trip data in CSV or JSON format")
    public ResponseEntity<String> exportTripData(
            @PathVariable String userId,
            @RequestParam(defaultValue = "csv") String format) {
        return ResponseEntity.ok(tripService.exportTripData(userId, format));
    }
}
