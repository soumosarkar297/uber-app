package com.rideshare.userservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.userservice.event.DocumentUploadedEvent;
import com.rideshare.userservice.event.DocumentVerifiedEvent;
import com.rideshare.userservice.event.UserProfileUpdatedEvent;
import com.rideshare.userservice.event.UserRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for publishing user-related events to Kafka topics.
 * Uses JSON serialization for event payloads.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.user-registered:user.registered}")
    private String userRegisteredTopic;

    @Value("${spring.kafka.topics.user-profile-updated:user.profile.updated}")
    private String userProfileUpdatedTopic;

    @Value("${spring.kafka.topics.document-uploaded:document.uploaded}")
    private String documentUploadedTopic;

    @Value("${spring.kafka.topics.document-verified:document.verified}")
    private String documentVerifiedTopic;

    /**
     * Publish user registered event.
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
     * Publish user profile updated event.
     */
    public void publishUserProfileUpdated(UserProfileUpdatedEvent event) {
        try {
            kafkaTemplate.send(userProfileUpdatedTopic, event.getUserId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish UserProfileUpdatedEvent for userId: {}", event.getUserId(), ex);
                        } else {
                            log.debug("Published UserProfileUpdatedEvent for userId: {} to topic: {} partition: {} offset: {}",
                                    event.getUserId(), result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing UserProfileUpdatedEvent for userId: {}", event.getUserId(), e);
        }
    }

    /**
     * Publish document uploaded event.
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
     * Publish document verified event.
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
