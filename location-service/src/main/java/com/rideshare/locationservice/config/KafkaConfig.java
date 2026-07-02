package com.rideshare.locationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic configuration for the location service.
 * Defines topics for location updates and analytics data.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic locationUpdatedTopic() {
        return TopicBuilder.name("location.updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic analyticsTopic() {
        return TopicBuilder.name("analytics.events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
