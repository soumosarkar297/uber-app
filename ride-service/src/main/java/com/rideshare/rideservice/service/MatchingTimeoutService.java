package com.rideshare.rideservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rideshare.rideservice.event.RideDeclinedEvent;
import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideCancellationReason;
import com.rideshare.rideservice.model.RideStatus;
import com.rideshare.rideservice.repository.RideRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Monitors rides stuck in MATCHING state and cancels them after a timeout.
 * Publishes ride.declined events so riders are notified.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingTimeoutService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideDeclinedEvent> kafkaTemplate;

    private static final long TIMEOUT_SECONDS = 120;
    private static final String RIDE_DECLINED_TOPIC = "ride.declined";

    /**
     * Checks every 30 seconds for rides stuck in MATCHING state
     * beyond the timeout threshold and cancels them.
     */
    @Scheduled(fixedRate = 30_000)
    public void checkTimedOutRides() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(TIMEOUT_SECONDS);
        List<Ride> timedOutRides = rideRepository.findByStatusAndCreatedAtBefore(
                RideStatus.MATCHING, cutoff);

        for (Ride ride : timedOutRides) {
            log.warn("Ride {} timed out in MATCHING state, cancelling", ride.getId());

            ride.setStatus(RideStatus.CANCELLED);
            ride.setCancellationReason(RideCancellationReason.NO_DRIVERS_AVAILABLE);
            ride.setCancelledBy("system");
            ride.setCancelledAt(LocalDateTime.now());
            rideRepository.save(ride);

            kafkaTemplate.send(RIDE_DECLINED_TOPIC, ride.getId(),
                    new RideDeclinedEvent(ride.getId(), "MATCHING_TIMEOUT", null));

            log.info("Ride {} cancelled due to matching timeout", ride.getId());
        }
    }
}
