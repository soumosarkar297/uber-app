package com.rideshare.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configures Kafka topics used by the ride service for event-driven communication.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class KafkaConfig {

    /** Creates the ride.requested topic. */
    @Bean
    public NewTopic rideRequestedTopic() {
        return TopicBuilder.name("ride.requested")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /** Creates the ride.matched topic. */
    @Bean
    public NewTopic rideMatchedTopic() {
        return TopicBuilder.name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /** Creates the ride.accepted topic. */
    @Bean
    public NewTopic rideAcceptedTopic() {
        return TopicBuilder.name("ride.accepted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /** Creates the ride.cancelled topic. */
    @Bean
    public NewTopic rideCancelledTopic() {
        return TopicBuilder.name("ride.cancelled")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /** Creates the ride.started topic. */
    @Bean
    public NewTopic rideStartedTopic() {
        return TopicBuilder.name("ride.started")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /** Creates the ride.completed topic. */
    @Bean
    public NewTopic rideCompletedTopic() {
        return TopicBuilder.name("ride.completed")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
