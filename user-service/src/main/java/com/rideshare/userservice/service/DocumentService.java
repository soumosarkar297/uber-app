package com.rideshare.userservice.service;

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

import com.rideshare.userservice.dto.DocumentResponse;
import com.rideshare.userservice.dto.DocumentUploadRequest;
import com.rideshare.userservice.entity.Document;
import com.rideshare.userservice.entity.DocumentType;
import com.rideshare.userservice.entity.VerificationStatus;
import com.rideshare.userservice.event.DocumentUploadedEvent;
import com.rideshare.userservice.event.DocumentVerifiedEvent;
import com.rideshare.userservice.repository.DocumentRepository;
import com.rideshare.userservice.service.UserEventPublisher;

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
    private final UserEventPublisher eventPublisher;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Upload a new document.
     */
    @Transactional
    public DocumentResponse uploadDocument(DocumentUploadRequest request) {
        DocumentType documentType = parseDocumentType(request.getDocumentType());

        Document document = new Document();
        document.setUserId(request.getUserId());
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
            savedDocument.getUserId(),
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
     * Get all documents for a user.
     */
    public List<DocumentResponse> getDocumentsByUserId(UUID userId) {
        return documentRepository.findByUserId(userId).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get documents for a user by type.
     */
    public List<DocumentResponse> getDocumentsByUserIdAndType(UUID userId, String documentType) {
        DocumentType type = parseDocumentType(documentType);
        return documentRepository.findByUserIdAndDocumentType(userId, type).stream()
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
     * Get documents for a user by verification status.
     */
    public List<DocumentResponse> getDocumentsByUserIdAndStatus(UUID userId, VerificationStatus status) {
        return documentRepository.findByUserIdAndVerificationStatus(userId, status).stream()
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
            updatedDocument.getUserId(),
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
     * Check if user has a document of specific type.
     */
    public boolean hasDocumentOfType(UUID userId, String documentType) {
        DocumentType type = parseDocumentType(documentType);
        return documentRepository.existsByUserIdAndDocumentType(userId, type);
    }

    /**
     * Count documents by user and verification status.
     */
    public long countByUserIdAndVerificationStatus(UUID userId, VerificationStatus status) {
        return documentRepository.countByUserIdAndVerificationStatus(userId, status);
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
        response.setUserId(document.getUserId());
        response.setDocumentType(document.getDocumentType().name());
        response.setFileUrl(document.getFileUrl());
        response.setFileName(document.getFileName());
        response.setFileSize(document.getFileSize());
        response.setMimeType(document.getMimeType());
        response.setVerificationStatus(document.getVerificationStatus().name());
        response.setRejectionReason(document.getRejectionReason());
        response.setVerifiedAt(document.getVerifiedAt());
        response.setVerifiedBy(document.getVerifiedBy());
        response.setExpiryDate(document.getExpiryDate());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());
        return response;
    }
}