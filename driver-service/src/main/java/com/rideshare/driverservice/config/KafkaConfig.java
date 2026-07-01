package com.rideshare.driverservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for driver service event publishing.
 * Defines topics for driver registration, user registration, and document events.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topics.driver-registered:driver.registered}")
    private String driverRegisteredTopic;

    @Value("${spring.kafka.topics.user-registered:user.registered}")
    private String userRegisteredTopic;

    @Value("${spring.kafka.topics.document-uploaded:document.uploaded}")
    private String documentUploadedTopic;

    @Value("${spring.kafka.topics.document-verified:document.verified}")
    private String documentVerifiedTopic;

    /**
     * Creates the Kafka topic for driver registration events.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    @Bean
    public NewTopic driverRegisteredTopic() {
        return TopicBuilder.name(driverRegisteredTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Creates the Kafka topic for user registration events.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Creates the Kafka topic for document upload events.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    @Bean
    public NewTopic documentUploadedTopic() {
        return TopicBuilder.name(documentUploadedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Creates the Kafka topic for document verification events.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    @Bean
    public NewTopic documentVerifiedTopic() {
        return TopicBuilder.name(documentVerifiedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
