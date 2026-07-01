package com.rideshare.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for user service event publishing.
 * Defines topics for user registration, profile updates, and document events.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topics.user-registered:user.registered}")
    private String userRegisteredTopic;

    @Value("${spring.kafka.topics.user-profile-updated:user.profile.updated}")
    private String userProfileUpdatedTopic;

    @Value("${spring.kafka.topics.document-uploaded:document.uploaded}")
    private String documentUploadedTopic;

    @Value("${spring.kafka.topics.document-verified:document.verified}")
    private String documentVerifiedTopic;

    /**
     * User registered topic.
     */
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * User profile updated topic.
     */
    @Bean
    public NewTopic userProfileUpdatedTopic() {
        return TopicBuilder.name(userProfileUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Document uploaded topic.
     */
    @Bean
    public NewTopic documentUploadedTopic() {
        return TopicBuilder.name(documentUploadedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Document verified topic.
     */
    @Bean
    public NewTopic documentVerifiedTopic() {
        return TopicBuilder.name(documentVerifiedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
