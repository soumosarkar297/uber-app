package com.rideshare.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration class for Kafka producer and consumer setup.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class KafkaConfig {

    /**
     * Creates the Kafka topic for ride requests.
     * <p>
     * The Ride Service publishes ride request events to this topic. The
     * Matching Service subscribes to consume these requests.
     * </p>
     *
     * @return a {@link NewTopic} configured with 3 partitions and 1 replica
     */
    @Bean
    public NewTopic rideRequestedTopic() {
        return TopicBuilder.name("ride.requested")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Creates the Kafka topic for ride match results.
     * <p>
     * The Matching Service publishes match result events to this topic. The
     * Ride Service subscribes to consume these results.
     * </p>
     *
     * @return a {@link NewTopic} configured with 3 partitions and 1 replica
     */
    @Bean
    public NewTopic rideMatchedTopic() {
        return TopicBuilder.name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
