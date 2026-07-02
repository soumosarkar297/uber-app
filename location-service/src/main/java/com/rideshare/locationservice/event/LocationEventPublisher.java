package com.rideshare.locationservice.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Publishes location-related events to Kafka for downstream consumers.
 * Events are published asynchronously to avoid blocking the location update path.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LocationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String LOCATION_UPDATED_TOPIC = "location.updated";
    private static final String ANALYTICS_TOPIC = "analytics.events";

    /**
     * Publishes a location updated event to Kafka.
     */
    public void publishLocationUpdated(LocationUpdatedEvent event) {
        kafkaTemplate.send(LOCATION_UPDATED_TOPIC, event.getDriverId(), event);
        log.debug("LocationUpdatedEvent published for driver: {}", event.getDriverId());
    }

    /**
     * Publishes an analytics event to the analytics topic.
     */
    public void publishAnalyticsEvent(Object event) {
        kafkaTemplate.send(ANALYTICS_TOPIC, "analytics", event);
        log.debug("Analytics event published");
    }
}
