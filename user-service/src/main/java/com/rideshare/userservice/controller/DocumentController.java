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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Document Management", description = "Document upload, verification, and management")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    @Operation(summary = "Upload Document", description = "Uploads a new document for verification")
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
    @Operation(summary = "Get Document by ID", description = "Returns a document by its UUID")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentById(@PathVariable UUID id) {
        log.info("Getting document by ID: {}", id);
        return documentService.getDocumentById(id)
                .map(doc -> ResponseEntity.ok(ApiResponse.success(doc)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Document not found", "DOCUMENT_NOT_FOUND")));
    }

    @GetMapping("/my")
    @Operation(summary = "Get My Documents", description = "Returns all documents for the authenticated user")
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
    @Operation(summary = "Get User Documents", description = "Returns all documents for a specific user")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getUserDocuments(@PathVariable UUID userId) {
        log.info("Getting documents for user: {}", userId);
        List<DocumentResponse> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get Pending Documents", description = "Returns all documents pending verification")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getPendingDocuments() {
        log.info("Getting pending documents for verification");
        List<DocumentResponse> documents = documentService.getPendingDocuments();
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get Documents by Status", description = "Returns documents filtered by verification status")
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
    @Operation(summary = "Approve Document", description = "Approves a document after verification")
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
    @Operation(summary = "Reject Document", description = "Rejects a document with a reason")
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
    @Operation(summary = "Mark Document as Expired", description = "Marks a document as expired")
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
    @Operation(summary = "Delete Document", description = "Deletes a document by its UUID")
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
