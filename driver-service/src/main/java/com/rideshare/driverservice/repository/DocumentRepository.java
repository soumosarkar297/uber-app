package com.rideshare.driverservice.repository;

/**
 * Repository for Document entity with custom query methods.
 * Manages driver documents for verification workflow.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rideshare.driverservice.entity.Document;
import com.rideshare.driverservice.entity.DocumentType;
import com.rideshare.driverservice.entity.VerificationStatus;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByDriverId(UUID driverId);

    List<Document> findByDriverIdAndDocumentType(UUID driverId, DocumentType documentType);

    List<Document> findByVerificationStatus(VerificationStatus verificationStatus);

    @Query("SELECT d FROM Document d WHERE d.driverId = :driverId AND d.verificationStatus = :status")
    List<Document> findByDriverIdAndVerificationStatus(@Param("driverId") UUID driverId, @Param("status") VerificationStatus status);

    boolean existsByDriverIdAndDocumentType(UUID driverId, DocumentType documentType);

    long countByDriverIdAndVerificationStatus(UUID driverId, VerificationStatus verificationStatus);
}
