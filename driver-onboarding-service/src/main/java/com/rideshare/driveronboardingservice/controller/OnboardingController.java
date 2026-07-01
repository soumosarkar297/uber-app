package com.rideshare.driveronboardingservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.driveronboardingservice.dto.ApplicationResponse;
import com.rideshare.driveronboardingservice.dto.CreateApplicationRequest;
import com.rideshare.driveronboardingservice.dto.DocumentResponse;
import com.rideshare.driveronboardingservice.dto.ReviewApplicationRequest;
import com.rideshare.driveronboardingservice.dto.UploadDocumentRequest;
import com.rideshare.driveronboardingservice.dto.VerifyDocumentRequest;
import com.rideshare.driveronboardingservice.service.DocumentService;
import com.rideshare.driveronboardingservice.service.OnboardingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for the driver onboarding workflow.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/onboarding")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Driver Onboarding", description = "Document upload, verification, vehicle registration, and approval workflow")
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final DocumentService documentService;

    // ── Application APIs ──

    @PostMapping("/application")
    @Operation(summary = "Create Application", description = "Creates a new onboarding application")
    public ResponseEntity<ApplicationResponse> createApplication(
            @Valid @RequestBody CreateApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(onboardingService.createApplication(request));
    }

    @PutMapping("/application/{applicationId}/submit")
    @Operation(summary = "Submit Application", description = "Submits a draft application for review")
    public ResponseEntity<ApplicationResponse> submitApplication(
            @PathVariable String applicationId) {
        return ResponseEntity.ok(onboardingService.submitApplication(applicationId));
    }

    @PutMapping("/application/{applicationId}/review")
    @Operation(summary = "Review Application", description = "Admin reviews and approves/rejects an application")
    public ResponseEntity<ApplicationResponse> reviewApplication(
            @PathVariable String applicationId,
            @Valid @RequestBody ReviewApplicationRequest request) {
        return ResponseEntity.ok(onboardingService.reviewApplication(applicationId, request));
    }

    @GetMapping("/application/driver/{driverId}")
    @Operation(summary = "Get Driver Application", description = "Gets the onboarding application for a driver")
    public ResponseEntity<ApplicationResponse> getApplication(
            @PathVariable String driverId) {
        return ResponseEntity.ok(onboardingService.getApplication(driverId));
    }

    @GetMapping("/applications/pending")
    @Operation(summary = "Get Pending Applications", description = "Gets all applications pending admin review")
    public ResponseEntity<List<ApplicationResponse>> getPendingApplications() {
        return ResponseEntity.ok(onboardingService.getPendingApplications());
    }

    // ── Document APIs ──

    @PostMapping("/document/upload")
    @Operation(summary = "Upload Document", description = "Uploads a document for verification")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @Valid @RequestBody UploadDocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.uploadDocument(request));
    }

    @PutMapping("/document/{documentId}/verify")
    @Operation(summary = "Verify Document", description = "Admin verifies or rejects a document")
    public ResponseEntity<DocumentResponse> verifyDocument(
            @PathVariable String documentId,
            @Valid @RequestBody VerifyDocumentRequest request) {
        return ResponseEntity.ok(documentService.verifyDocument(documentId, request));
    }

    @GetMapping("/documents/driver/{driverId}")
    @Operation(summary = "Get Driver Documents", description = "Gets all documents for a driver")
    public ResponseEntity<List<DocumentResponse>> getDriverDocuments(
            @PathVariable String driverId) {
        return ResponseEntity.ok(documentService.getDriverDocuments(driverId));
    }

    @GetMapping("/documents/application/{applicationId}")
    @Operation(summary = "Get Application Documents", description = "Gets all documents for an application")
    public ResponseEntity<List<DocumentResponse>> getApplicationDocuments(
            @PathVariable String applicationId) {
        return ResponseEntity.ok(documentService.getApplicationDocuments(applicationId));
    }
}
