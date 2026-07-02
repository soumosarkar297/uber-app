package com.rideshare.paymentservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.paymentservice.dto.RidePaymentRequest;
import com.rideshare.paymentservice.event.RideEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer that listens for ride lifecycle events and triggers payment processing.
 * When a ride is completed, automatically processes the payment using the rider's default
 * payment method (wallet if available, otherwise cash).
 *
 * Flow: ride.completed → RideEventConsumer → processRidePayment → PaymentCompletedEvent
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final RidePaymentService ridePaymentService;

    /**
     * Listens for ride.completed events and triggers automatic payment processing.
     */
    @KafkaListener(topics = "ride.completed", groupId = "payment-service-group")
    public void onRideCompleted(RideEvent event) {
        try {
            log.info("Ride completed, processing payment for ride: {} amount: {}",
                    event.getRideId(), event.getActualFare());

            RidePaymentRequest paymentRequest = new RidePaymentRequest();
            paymentRequest.setRideId(event.getRideId());
            paymentRequest.setRiderId(event.getRiderId());
            paymentRequest.setDriverId(event.getDriverId());
            paymentRequest.setAmount(java.math.BigDecimal.valueOf(event.getActualFare()));
            paymentRequest.setPaymentMethod(
                    event.getPaymentMethod() != null ? event.getPaymentMethod() : "WALLET");

            ridePaymentService.processRidePayment(paymentRequest);

            log.info("Payment processed successfully for ride: {}", event.getRideId());

        } catch (Exception e) {
            log.error("Failed to process payment for ride: {} - {}",
                    event.getRideId(), e.getMessage());
        }
    }
}
