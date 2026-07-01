package com.rideshare.userservice.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.userservice.context.UserContext;
import com.rideshare.userservice.dto.ApiResponse;
import com.rideshare.userservice.dto.DocumentResponse;
import com.rideshare.userservice.dto.DocumentUploadRequest;
import com.rideshare.userservice.entity.User;
import com.rideshare.userservice.entity.VerificationStatus;
import com.rideshare.userservice.service.DocumentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(@Valid @RequestBody DocumentUploadRequest request) {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        log.info("Uploading document for user: {} type: {}", currentUser.getId(), request.getDocumentType());
        try {
            DocumentResponse response = documentService.uploadDocument(request);
            return ResponseEntity.status(201).body(ApiResponse.success("Document uploaded successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentById(@PathVariable UUID id) {
        log.info("Getting document by ID: {}", id);
        return documentService.getDocumentById(id)
                .map(doc -> ResponseEntity.ok(ApiResponse.success(doc)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Document not found", "DOCUMENT_NOT_FOUND")));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getMyDocuments(
            @RequestParam(required = false) String type) {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated", "UNAUTHENTICATED"));
        }
        UUID userId = currentUser.getId();
        log.info("Getting documents for user: {} type: {}", userId, type);
        List<DocumentResponse> documents;
        if (type != null && !type.isBlank()) {
            documents = documentService.getDocumentsByUserIdAndType(userId, type);
        } else {
            documents = documentService.getDocumentsByUserId(userId);
        }
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getUserDocuments(@PathVariable UUID userId) {
        log.info("Getting documents for user: {}", userId);
        List<DocumentResponse> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getPendingDocuments() {
        log.info("Getting pending documents for verification");
        List<DocumentResponse> documents = documentService.getPendingDocuments();
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByStatus(@PathVariable String status) {
        log.info("Getting documents with status: {}", status);
        try {
            VerificationStatus verificationStatus = VerificationStatus.valueOf(status.toUpperCase());
            List<DocumentResponse> documents = documentService.getDocumentsByVerificationStatus(verificationStatus);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid status: " + status, "INVALID_STATUS"));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<DocumentResponse>> approveDocument(
            @PathVariable UUID id,
            @RequestParam String verifiedBy) {
        log.info("Approving document: {} by: {}", id, verifiedBy);
        try {
            DocumentResponse response = documentService.approveDocument(id, verifiedBy);
            return ResponseEntity.ok(ApiResponse.success("Document approved successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<DocumentResponse>> rejectDocument(
            @PathVariable UUID id,
            @RequestParam String verifiedBy,
            @RequestParam String rejectionReason) {
        log.info("Rejecting document: {} by: {} reason: {}", id, verifiedBy, rejectionReason);
        try {
            DocumentResponse response = documentService.rejectDocument(id, verifiedBy, rejectionReason);
            return ResponseEntity.ok(ApiResponse.success("Document rejected successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @PutMapping("/{id}/expire")
    public ResponseEntity<ApiResponse<DocumentResponse>> markAsExpired(@PathVariable UUID id) {
        log.info("Marking document as expired: {}", id);
        try {
            DocumentResponse response = documentService.markAsExpired(id);
            return ResponseEntity.ok(ApiResponse.success("Document marked as expired", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDocument(@PathVariable UUID id) {
        log.info("Deleting document: {}", id);
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(ApiResponse.success("Document deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage(), "DOCUMENT_NOT_FOUND"));
        }
    }
}
