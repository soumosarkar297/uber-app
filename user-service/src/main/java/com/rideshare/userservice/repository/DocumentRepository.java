package com.rideshare.userservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rideshare.userservice.entity.Document;
import com.rideshare.userservice.entity.DocumentType;
import com.rideshare.userservice.entity.VerificationStatus;

/**
 * Repository for Document entity with custom query methods.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByUserId(UUID userId);

    List<Document> findByUserIdAndDocumentType(UUID userId, DocumentType documentType);

    List<Document> findByVerificationStatus(VerificationStatus verificationStatus);

    @Query("SELECT d FROM Document d WHERE d.userId = :userId AND d.verificationStatus = :status")
    List<Document> findByUserIdAndVerificationStatus(@Param("userId") UUID userId, @Param("status") VerificationStatus status);

    boolean existsByUserIdAndDocumentType(UUID userId, DocumentType documentType);

    long countByUserIdAndVerificationStatus(UUID userId, VerificationStatus verificationStatus);
}