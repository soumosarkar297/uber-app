package com.rideshare.tripservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.tripservice.dto.RideReceiptResponse;
import com.rideshare.tripservice.dto.TripAnalyticsResponse;
import com.rideshare.tripservice.dto.TripRecordResponse;
import com.rideshare.tripservice.entity.TripRecord;
import com.rideshare.tripservice.repository.TripRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Core service for trip record management and analytics.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TripService {

    private final TripRecordRepository tripRecordRepository;

    /**
     * Handles a ride completed event from Kafka and persists the trip record.
     */
    @KafkaListener(topics = "ride.completed", groupId = "trip-service-group")
    public void onRideCompleted(com.rideshare.tripservice.event.RideEvent event) {
        TripRecord record = new TripRecord();
        record.setRideId(event.getRideId());
        record.setRiderId(event.getRiderId());
        record.setDriverId(event.getDriverId());
        record.setPickupAddress(event.getPickupAddress());
        record.setDropAddress(event.getDropAddress());
        record.setDistanceKm(event.getDistanceKm());
        record.setDurationMinutes(event.getDurationMinutes());
        record.setStatus("COMPLETED");
        record.setActualFare(BigDecimal.valueOf(event.getFare()));
        record.setPaymentMethod(event.getPaymentMethod());
        record.setRequestedAt(event.getRequestedAt());
        record.setStartedAt(event.getStartedAt());
        record.setCompletedAt(event.getCompletedAt());

        tripRecordRepository.save(record);
        log.info("Trip record saved for ride: {}", event.getRideId());
    }

    /**
     * Handles a ride cancelled event from Kafka and persists the trip record.
     */
    @KafkaListener(topics = "ride.cancelled", groupId = "trip-service-group")
    public void onRideCancelled(com.rideshare.tripservice.event.RideEvent event) {
        TripRecord record = new TripRecord();
        record.setRideId(event.getRideId());
        record.setRiderId(event.getRiderId());
        record.setDriverId(event.getDriverId());
        record.setStatus("CANCELLED");
        record.setCancelledAt(event.getCompletedAt());

        tripRecordRepository.save(record);
        log.info("Cancelled trip record saved for ride: {}", event.getRideId());
    }

    /**
     * Retrieves paginated trip history for the specified rider.
     */
    public List<TripRecordResponse> getRiderTripHistory(String riderId, int page, int size) {
        return tripRecordRepository
                .findByRiderIdOrderByCreatedAtDesc(riderId, PageRequest.of(page, size))
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Retrieves paginated trip history for the specified driver.
     */
    public List<TripRecordResponse> getDriverTripHistory(String driverId, int page, int size) {
        return tripRecordRepository
                .findByDriverIdOrderByCreatedAtDesc(driverId, PageRequest.of(page, size))
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Calculates and returns aggregated trip analytics for the specified rider.
     */
    public TripAnalyticsResponse getRiderAnalytics(String riderId) {
        List<TripRecord> records = tripRecordRepository
                .findByRiderIdOrderByCreatedAtDesc(riderId, PageRequest.of(0, 1000));
        return calculateAnalytics(riderId, records);
    }

    /**
     * Calculates and returns aggregated trip analytics for the specified driver.
     */
    public TripAnalyticsResponse getDriverAnalytics(String driverId) {
        List<TripRecord> records = tripRecordRepository
                .findByDriverIdOrderByCreatedAtDesc(driverId, PageRequest.of(0, 1000));
        return calculateAnalytics(driverId, records);
    }

    /**
     * Retrieves the detailed receipt for a completed ride.
     */
    public RideReceiptResponse getRideReceipt(String rideId) {
        TripRecord record = tripRecordRepository.findByRideId(rideId)
                .orElseThrow(() -> new RuntimeException("Trip not found for ride: " + rideId));

        RideReceiptResponse receipt = new RideReceiptResponse();
        receipt.setRideId(record.getRideId());
        receipt.setRiderName(record.getRiderName());
        receipt.setDriverName(record.getDriverName());
        receipt.setVehicleType(record.getVehicleType());
        receipt.setPickupAddress(record.getPickupAddress());
        receipt.setDropAddress(record.getDropAddress());
        receipt.setDistanceKm(record.getDistanceKm());
        receipt.setDurationMinutes(record.getDurationMinutes());
        receipt.setFare(record.getActualFare());
        receipt.setTotalAmount(record.getActualFare());
        receipt.setPaymentMethod(record.getPaymentMethod());
        receipt.setRideDate(record.getCompletedAt());
        return receipt;
    }

    /**
     * Exports trip data for the specified user in CSV or JSON format.
     */
    public String exportTripData(String userId, String format) {
        List<TripRecord> records = tripRecordRepository
                .findByRiderIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 10000));

        if ("csv".equalsIgnoreCase(format)) {
            return exportToCsv(records);
        }
        return exportToJson(records);
    }

    private TripAnalyticsResponse calculateAnalytics(String userId, List<TripRecord> records) {
        TripAnalyticsResponse analytics = new TripAnalyticsResponse();
        analytics.setUserId(userId);
        analytics.setTotalTrips(records.size());

        double totalDistance = 0;
        double totalDuration = 0;
        BigDecimal totalFare = BigDecimal.ZERO;
        int completed = 0;
        int cancelled = 0;

        for (TripRecord record : records) {
            totalDistance += record.getDistanceKm();
            totalDuration += record.getDurationMinutes();
            if (record.getActualFare() != null) {
                totalFare = totalFare.add(record.getActualFare());
            }
            if ("COMPLETED".equals(record.getStatus())) completed++;
            if ("CANCELLED".equals(record.getStatus())) cancelled++;
        }

        analytics.setTotalDistanceKm(Math.round(totalDistance * 100.0) / 100.0);
        analytics.setTotalDurationMinutes(Math.round(totalDuration * 10.0) / 10.0);
        analytics.setTotalFare(totalFare);
        analytics.setAverageFare(records.isEmpty() ? BigDecimal.ZERO :
                totalFare.divide(BigDecimal.valueOf(Math.max(records.size(), 1)), 2, RoundingMode.HALF_UP));
        analytics.setCompletedTrips(completed);
        analytics.setCancelledTrips(cancelled);
        return analytics;
    }

    private String exportToCsv(List<TripRecord> records) {
        StringBuilder csv = new StringBuilder();
        csv.append("Ride ID,Pickup,Drop,Distance,Duration,Fare,Status,Date\n");
        for (TripRecord r : records) {
            csv.append(String.format("%s,\"%s\",\"%s\",%.1f,%.1f,%.2f,%s,%s\n",
                    r.getRideId(), r.getPickupAddress(), r.getDropAddress(),
                    r.getDistanceKm(), r.getDurationMinutes(),
                    r.getActualFare() != null ? r.getActualFare().doubleValue() : 0,
                    r.getStatus(), r.getCreatedAt()));
        }
        return csv.toString();
    }

    private String exportToJson(List<TripRecord> records) {
        return records.stream().map(this::mapToResponse).toList().toString();
    }

    private TripRecordResponse mapToResponse(TripRecord record) {
        TripRecordResponse response = new TripRecordResponse();
        response.setId(record.getId());
        response.setRideId(record.getRideId());
        response.setRiderId(record.getRiderId());
        response.setDriverId(record.getDriverId());
        response.setDriverName(record.getDriverName());
        response.setPickupAddress(record.getPickupAddress());
        response.setPickupLatitude(record.getPickupLatitude());
        response.setPickupLongitude(record.getPickupLongitude());
        response.setDropAddress(record.getDropAddress());
        response.setDropLatitude(record.getDropLatitude());
        response.setDropLongitude(record.getDropLongitude());
        response.setDistanceKm(record.getDistanceKm());
        response.setDurationMinutes(record.getDurationMinutes());
        response.setStatus(record.getStatus());
        response.setEstimatedFare(record.getEstimatedFare());
        response.setActualFare(record.getActualFare());
        response.setPaymentMethod(record.getPaymentMethod());
        response.setSurgeMultiplier(record.getSurgeMultiplier());
        response.setVehicleType(record.getVehicleType());
        response.setRequestedAt(record.getRequestedAt());
        response.setStartedAt(record.getStartedAt());
        response.setCompletedAt(record.getCompletedAt());
        return response;
    }
}
