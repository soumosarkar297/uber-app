package com.rideshare.driverservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.driverservice.event.DocumentUploadedEvent;
import com.rideshare.driverservice.event.DocumentVerifiedEvent;
import com.rideshare.driverservice.event.DriverRegisteredEvent;
import com.rideshare.driverservice.event.UserRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for publishing driver-related events to Kafka topics. Uses JSON
 * serialization for event payloads.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.driver-registered:driver.registered}")
    private String driverRegisteredTopic;

    @Value("${spring.kafka.topics.user-registered:user.registered}")
    private String userRegisteredTopic;

    @Value("${spring.kafka.topics.document-uploaded:document.uploaded}")
    private String documentUploadedTopic;

    @Value("${spring.kafka.topics.document-verified:document.verified}")
    private String documentVerifiedTopic;

    /**
     * Publishes a driver registration event to the configured Kafka topic.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public void publishDriverRegistered(DriverRegisteredEvent event) {
        try {
            kafkaTemplate.send(driverRegisteredTopic, event.getUserId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish DriverRegisteredEvent for userId: {}", event.getUserId(), ex);
                        } else {
                            log.debug("Published DriverRegisteredEvent for userId: {} to topic: {} partition: {} offset: {}",
                                    event.getUserId(), result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing DriverRegisteredEvent for userId: {}", event.getUserId(), e);
        }
    }

    /**
     * Publishes a user registration event to the configured Kafka topic.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public void publishUserRegistered(UserRegisteredEvent event) {
        try {
            kafkaTemplate.send(userRegisteredTopic, event.getUserId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish UserRegisteredEvent for userId: {}", event.getUserId(), ex);
                        } else {
                            log.debug("Published UserRegisteredEvent for userId: {} to topic: {} partition: {} offset: {}",
                                    event.getUserId(), result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing UserRegisteredEvent for userId: {}", event.getUserId(), e);
        }
    }

    /**
     * Publishes a document upload event to the configured Kafka topic.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public void publishDocumentUploaded(DocumentUploadedEvent event) {
        try {
            kafkaTemplate.send(documentUploadedTopic, event.getDocumentId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish DocumentUploadedEvent for documentId: {}", event.getDocumentId(), ex);
                        } else {
                            log.debug("Published DocumentUploadedEvent for documentId: {} to topic: {} partition: {} offset: {}",
                                    event.getDocumentId(), result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing DocumentUploadedEvent for documentId: {}", event.getDocumentId(), e);
        }
    }

    /**
     * Publishes a document verification event to the configured Kafka topic.
     *
     * @author Soumo Sarkar
     * @version 1.0.0
     * @since 1.0.0
     */
    public void publishDocumentVerified(DocumentVerifiedEvent event) {
        try {
            kafkaTemplate.send(documentVerifiedTopic, event.getDocumentId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish DocumentVerifiedEvent for documentId: {}", event.getDocumentId(), ex);
                        } else {
                            log.debug("Published DocumentVerifiedEvent for documentId: {} to topic: {} partition: {} offset: {}",
                                    event.getDocumentId(), result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing DocumentVerifiedEvent for documentId: {}", event.getDocumentId(), e);
        }
    }
}
