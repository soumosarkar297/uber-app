package com.rideshare.driverservice.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.driverservice.dto.DocumentResponse;
import com.rideshare.driverservice.dto.DocumentUploadRequest;
import com.rideshare.driverservice.entity.Document;
import com.rideshare.driverservice.entity.DocumentType;
import com.rideshare.driverservice.entity.VerificationStatus;
import com.rideshare.driverservice.event.DocumentUploadedEvent;
import com.rideshare.driverservice.event.DocumentVerifiedEvent;
import com.rideshare.driverservice.repository.DocumentRepository;
import com.rideshare.driverservice.service.DriverEventPublisher;

import lombok.RequiredArgsConstructor;

/**
 * Service for document management.
 * Handles document upload, retrieval, and verification workflow.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DriverEventPublisher eventPublisher;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Upload a new document.
     */
    @Transactional
    public DocumentResponse uploadDocument(DocumentUploadRequest request) {
        DocumentType documentType = parseDocumentType(request.getDocumentType());

        Document document = new Document();
        document.setDriverId(request.getDriverId());
        document.setDocumentType(documentType);
        document.setFileUrl(request.getFileUrl());
        document.setFileName(request.getFileName());
        document.setFileSize(request.getFileSize());
        document.setMimeType(request.getMimeType());
        document.setVerificationStatus(VerificationStatus.PENDING);

        if (request.getExpiryDate() != null && !request.getExpiryDate().isBlank()) {
            document.setExpiryDate(LocalDate.parse(request.getExpiryDate(), DATE_FORMATTER));
        }

        Document savedDocument = documentRepository.save(document);
        eventPublisher.publishDocumentUploaded(new DocumentUploadedEvent(
            savedDocument.getId(),
            savedDocument.getDriverId(),
            savedDocument.getDocumentType().name(),
            Instant.now()
        ));
        return mapToDocumentResponse(savedDocument);
    }

    /**
     * Get document by ID.
     */
    public Optional<DocumentResponse> getDocumentById(UUID documentId) {
        return documentRepository.findById(documentId)
                .map(this::mapToDocumentResponse);
    }

    /**
     * Get all documents for a driver.
     */
    public List<DocumentResponse> getDocumentsByDriverId(UUID driverId) {
        return documentRepository.findByDriverId(driverId).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get documents for a driver by type.
     */
    public List<DocumentResponse> getDocumentsByDriverIdAndType(UUID driverId, String documentType) {
        DocumentType type = parseDocumentType(documentType);
        return documentRepository.findByDriverIdAndDocumentType(driverId, type).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get documents by verification status.
     */
    public List<DocumentResponse> getDocumentsByVerificationStatus(VerificationStatus status) {
        return documentRepository.findByVerificationStatus(status).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get documents for a driver by verification status.
     */
    public List<DocumentResponse> getDocumentsByDriverIdAndStatus(UUID driverId, VerificationStatus status) {
        return documentRepository.findByDriverIdAndVerificationStatus(driverId, status).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update document verification status.
     */
    @Transactional
    public DocumentResponse updateVerificationStatus(UUID documentId, VerificationStatus status, String verifiedBy, String rejectionReason) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));

        document.setVerificationStatus(status);
        document.setVerifiedAt(LocalDateTime.now());
        document.setVerifiedBy(verifiedBy);

        if (status == VerificationStatus.REJECTED && rejectionReason != null) {
            document.setRejectionReason(rejectionReason);
        } else if (status == VerificationStatus.VERIFIED) {
            document.setRejectionReason(null);
        }

        Document updatedDocument = documentRepository.save(document);
        eventPublisher.publishDocumentVerified(new DocumentVerifiedEvent(
            updatedDocument.getId(),
            updatedDocument.getDriverId(),
            updatedDocument.getVerificationStatus().name(),
            Instant.now()
        ));
        return mapToDocumentResponse(updatedDocument);
    }

    /**
     * Approve a document.
     */
    @Transactional
    public DocumentResponse approveDocument(UUID documentId, String verifiedBy) {
        return updateVerificationStatus(documentId, VerificationStatus.VERIFIED, verifiedBy, null);
    }

    /**
     * Reject a document.
     */
    @Transactional
    public DocumentResponse rejectDocument(UUID documentId, String verifiedBy, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required when rejecting a document");
        }
        return updateVerificationStatus(documentId, VerificationStatus.REJECTED, verifiedBy, rejectionReason);
    }

    /**
     * Mark document as expired.
     */
    @Transactional
    public DocumentResponse markAsExpired(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));

        document.setVerificationStatus(VerificationStatus.EXPIRED);
        document.setVerifiedAt(LocalDateTime.now());

        Document updatedDocument = documentRepository.save(document);
        return mapToDocumentResponse(updatedDocument);
    }

    /**
     * Delete a document.
     */
    @Transactional
    public void deleteDocument(UUID documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new IllegalArgumentException("Document not found with id: " + documentId);
        }
        documentRepository.deleteById(documentId);
    }

    /**
     * Check if driver has a document of specific type.
     */
    public boolean hasDocumentOfType(UUID driverId, String documentType) {
        DocumentType type = parseDocumentType(documentType);
        return documentRepository.existsByDriverIdAndDocumentType(driverId, type);
    }

    /**
     * Count documents by driver and verification status.
     */
    public long countByDriverIdAndVerificationStatus(UUID driverId, VerificationStatus status) {
        return documentRepository.countByDriverIdAndVerificationStatus(driverId, status);
    }

    /**
     * Get pending documents for verification (admin use).
     */
    public List<DocumentResponse> getPendingDocuments() {
        return documentRepository.findByVerificationStatus(VerificationStatus.PENDING).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check if all required driver documents are verified.
     */
    public boolean areAllDriverDocumentsVerified(UUID driverId) {
        long totalRequired = 3; // DRIVING_LICENSE, VEHICLE_REGISTRATION, VEHICLE_INSURANCE
        long verifiedCount = documentRepository.countByDriverIdAndVerificationStatus(driverId, VerificationStatus.VERIFIED);
        return verifiedCount >= totalRequired;
    }

    /**
     * Parse document type string to enum.
     */
    private DocumentType parseDocumentType(String documentType) {
        try {
            return DocumentType.valueOf(documentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type: " + documentType);
        }
    }

    /**
     * Map Document entity to DocumentResponse DTO.
     */
    private DocumentResponse mapToDocumentResponse(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setDriverId(document.getDriverId());
        response.setDocumentType(document.getDocumentType().name());
        response.setFileUrl(document.getFileUrl());
        response.setFileName(document.getFileName());
        response.setFileSize(document.getFileSize());
        response.setMimeType(document.getMimeType());
        response.setVerificationStatus(document.getVerificationStatus().name());
        response.setRejectionReason(document.getRejectionReason());
        response.setVerifiedAt(document.getVerifiedAt());
        response.setVerifiedBy(document.getVerifiedBy());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());
        return response;
    }
}
