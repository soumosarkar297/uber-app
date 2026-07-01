package com.rideshare.driveronboardingservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.driveronboardingservice.dto.DocumentResponse;
import com.rideshare.driveronboardingservice.dto.UploadDocumentRequest;
import com.rideshare.driveronboardingservice.dto.VerifyDocumentRequest;
import com.rideshare.driveronboardingservice.entity.UploadedDocument;
import com.rideshare.driveronboardingservice.entity.UploadedDocument.DocumentStatus;
import com.rideshare.driveronboardingservice.entity.UploadedDocument.DocumentType;
import com.rideshare.driveronboardingservice.repository.UploadedDocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages document upload and verification for driver onboarding.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final UploadedDocumentRepository documentRepository;

    /**
     * Persists a new document submission in PENDING status.
     */
    @Transactional
    public DocumentResponse uploadDocument(UploadDocumentRequest request) {
        UploadedDocument document = new UploadedDocument();
        document.setDriverId(request.getDriverId());
        document.setApplicationId(request.getApplicationId());
        document.setDocumentType(DocumentType.valueOf(request.getDocumentType()));
        document.setFileName(request.getFileName());
        document.setFileUrl(request.getFileUrl());
        document.setFileMimeType(request.getFileMimeType());
        document.setFileSizeBytes(request.getFileSizeBytes());
        document.setStatus(DocumentStatus.PENDING);

        UploadedDocument saved = documentRepository.save(document);
        log.info("Document uploaded for driver: {} type: {}", request.getDriverId(),
                request.getDocumentType());

        return mapToResponse(saved);
    }

    /**
     * Approves or rejects a previously uploaded document.
     */
    @Transactional
    public DocumentResponse verifyDocument(String documentId, VerifyDocumentRequest request) {
        UploadedDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        switch (request.getDecision().toUpperCase()) {
            case "APPROVE" -> {
                document.setStatus(DocumentStatus.APPROVED);
                document.setVerifiedBy(request.getVerifiedBy());
                document.setVerifiedAt(LocalDateTime.now());
            }
            case "REJECT" -> {
                document.setStatus(DocumentStatus.REJECTED);
                document.setRejectionReason(request.getRejectionReason());
                document.setVerifiedBy(request.getVerifiedBy());
                document.setVerifiedAt(LocalDateTime.now());
            }
            default -> throw new RuntimeException("Invalid decision: " + request.getDecision());
        }

        UploadedDocument saved = documentRepository.save(document);
        log.info("Document {} verified with decision: {}", documentId, request.getDecision());

        return mapToResponse(saved);
    }

    /**
     * Retrieves all documents uploaded by the given driver, ordered by creation date descending.
     */
    public List<DocumentResponse> getDriverDocuments(String driverId) {
        return documentRepository.findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Retrieves all documents associated with the given application.
     */
    public List<DocumentResponse> getApplicationDocuments(String applicationId) {
        return documentRepository.findByApplicationId(applicationId)
                .stream().map(this::mapToResponse).toList();
    }

    private DocumentResponse mapToResponse(UploadedDocument doc) {
        DocumentResponse response = new DocumentResponse();
        response.setId(doc.getId());
        response.setDriverId(doc.getDriverId());
        response.setApplicationId(doc.getApplicationId());
        response.setDocumentType(doc.getDocumentType().name());
        response.setFileName(doc.getFileName());
        response.setFileUrl(doc.getFileUrl());
        response.setStatus(doc.getStatus().name());
        response.setRejectionReason(doc.getRejectionReason());
        response.setVerifiedAt(doc.getVerifiedAt());
        response.setCreatedAt(doc.getCreatedAt());
        return response;
    }
}
