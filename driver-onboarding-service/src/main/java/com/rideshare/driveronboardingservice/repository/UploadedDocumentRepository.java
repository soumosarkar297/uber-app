package com.rideshare.driveronboardingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.driveronboardingservice.entity.UploadedDocument;

/**
 * Repository for accessing uploaded document persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UploadedDocumentRepository extends JpaRepository<UploadedDocument, String> {

    List<UploadedDocument> findByDriverIdOrderByCreatedAtDesc(String driverId);

    List<UploadedDocument> findByApplicationId(String applicationId);

    long countByApplicationIdAndStatus(String applicationId, UploadedDocument.DocumentStatus status);
}
