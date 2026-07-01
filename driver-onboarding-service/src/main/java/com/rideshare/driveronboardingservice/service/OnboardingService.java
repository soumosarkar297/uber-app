package com.rideshare.driveronboardingservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.driveronboardingservice.dto.ApplicationResponse;
import com.rideshare.driveronboardingservice.dto.CreateApplicationRequest;
import com.rideshare.driveronboardingservice.dto.ReviewApplicationRequest;
import com.rideshare.driveronboardingservice.entity.OnboardingApplication;
import com.rideshare.driveronboardingservice.entity.OnboardingApplication.ApplicationStatus;
import com.rideshare.driveronboardingservice.entity.OnboardingApplication.VerificationStatus;
import com.rideshare.driveronboardingservice.entity.Vehicle;
import com.rideshare.driveronboardingservice.repository.OnboardingApplicationRepository;
import com.rideshare.driveronboardingservice.repository.UploadedDocumentRepository;
import com.rideshare.driveronboardingservice.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates the driver onboarding workflow from application creation to approval.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingApplicationRepository applicationRepository;
    private final UploadedDocumentRepository documentRepository;
    private final VehicleRepository vehicleRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Creates a new onboarding application in DRAFT status and registers the vehicle if provided.
     */
    @Transactional
    public ApplicationResponse createApplication(CreateApplicationRequest request) {
        var existing = applicationRepository.findByDriverId(request.getDriverId());
        if (existing.isPresent() && existing.get().getStatus() != ApplicationStatus.REJECTED) {
            throw new RuntimeException("Driver already has an active application");
        }

        OnboardingApplication application = new OnboardingApplication();
        application.setDriverId(request.getDriverId());
        application.setStatus(ApplicationStatus.DRAFT);
        application.setVehicleType(request.getVehicleType());
        application.setVehicleMake(request.getVehicleMake());
        application.setVehicleModel(request.getVehicleModel());
        application.setVehicleYear(request.getVehicleYear());
        application.setVehicleColor(request.getVehicleColor());
        application.setLicensePlateNumber(request.getLicensePlateNumber());
        application.setLicenseNumber(request.getLicenseNumber());
        application.setInsuranceProvider(request.getInsuranceProvider());
        application.setInsurancePolicyNumber(request.getInsurancePolicyNumber());
        application.setBackgroundCheckStatus(VerificationStatus.PENDING);

        OnboardingApplication saved = applicationRepository.save(application);
        log.info("Onboarding application created for driver: {}", request.getDriverId());

        // Register vehicle
        if (request.getLicensePlateNumber() != null) {
            Vehicle vehicle = new Vehicle();
            vehicle.setDriverId(request.getDriverId());
            vehicle.setRegistrationNumber(request.getLicensePlateNumber());
            vehicle.setMake(request.getVehicleMake() != null ? request.getVehicleMake() : "");
            vehicle.setModel(request.getVehicleModel() != null ? request.getVehicleModel() : "");
            vehicle.setYear(request.getVehicleYear());
            vehicle.setColor(request.getVehicleColor());
            vehicle.setType(request.getVehicleType() != null ? request.getVehicleType() : "SEDAN");
            vehicle.setStatus(Vehicle.VehicleStatus.PENDING_VERIFICATION);
            vehicleRepository.save(vehicle);
        }

        return mapToResponse(saved);
    }

    /**
     * Submits a DRAFT application for admin review after verifying at least three approved documents.
     */
    public ApplicationResponse submitApplication(String applicationId) {
        OnboardingApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT applications can be submitted");
        }

        long approvedDocs = documentRepository.countByApplicationIdAndStatus(
                applicationId, com.rideshare.driveronboardingservice.entity.UploadedDocument.DocumentStatus.APPROVED);

        if (approvedDocs < 3) {
            throw new RuntimeException("At least 3 approved documents required to submit");
        }

        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setBackgroundCheckStatus(VerificationStatus.IN_PROGRESS);

        OnboardingApplication saved = applicationRepository.save(application);
        log.info("Application submitted for driver: {}", application.getDriverId());

        return mapToResponse(saved);
    }

    /**
     * Processes an admin review decision to approve, reject, or request resubmission of an application.
     */
    @Transactional
    public ApplicationResponse reviewApplication(String applicationId, ReviewApplicationRequest request) {
        OnboardingApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        switch (request.getDecision().toUpperCase()) {
            case "APPROVE" -> {
                application.setStatus(ApplicationStatus.APPROVED);
                application.setBackgroundCheckStatus(VerificationStatus.VERIFIED);
                application.setReviewedBy(request.getReviewedBy());
                application.setReviewedAt(LocalDateTime.now());
                application.setAdminNotes(request.getAdminNotes());

                // Activate vehicle
                vehicleRepository.findByDriverId(application.getDriverId()).ifPresent(vehicle -> {
                    vehicle.setStatus(Vehicle.VehicleStatus.ACTIVE);
                    vehicleRepository.save(vehicle);
                });

                // Publish event
                kafkaTemplate.send("driver.onboarded", application.getDriverId(),
                        Map.of("driverId", application.getDriverId(), "status", "APPROVED"));
            }
            case "REJECT" -> {
                application.setStatus(ApplicationStatus.REJECTED);
                application.setReviewedBy(request.getReviewedBy());
                application.setReviewedAt(LocalDateTime.now());
                application.setAdminNotes(request.getAdminNotes());
            }
            case "RESUBMIT" -> {
                application.setStatus(ApplicationStatus.RESUBMIT_REQUIRED);
                application.setReviewedBy(request.getReviewedBy());
                application.setReviewedAt(LocalDateTime.now());
                application.setAdminNotes(request.getAdminNotes());
            }
            default -> throw new RuntimeException("Invalid decision: " + request.getDecision());
        }

        OnboardingApplication saved = applicationRepository.save(application);
        log.info("Application {} reviewed with decision: {}", applicationId, request.getDecision());

        return mapToResponse(saved);
    }

    /**
     * Retrieves the onboarding application for the given driver.
     */
    public ApplicationResponse getApplication(String driverId) {
        OnboardingApplication application = applicationRepository.findByDriverId(driverId)
                .orElseThrow(() -> new RuntimeException("No application found for driver: " + driverId));
        return mapToResponse(application);
    }

    /**
     * Retrieves all applications currently in SUBMITTED status awaiting admin review.
     */
    public List<ApplicationResponse> getPendingApplications() {
        return applicationRepository.findByStatus(ApplicationStatus.SUBMITTED)
                .stream().map(this::mapToResponse).toList();
    }

    private ApplicationResponse mapToResponse(OnboardingApplication app) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(app.getId());
        response.setDriverId(app.getDriverId());
        response.setStatus(app.getStatus().name());
        response.setVehicleType(app.getVehicleType());
        response.setVehicleMake(app.getVehicleMake());
        response.setVehicleModel(app.getVehicleModel());
        response.setVehicleYear(app.getVehicleYear());
        response.setVehicleColor(app.getVehicleColor());
        response.setLicensePlateNumber(app.getLicensePlateNumber());
        response.setLicenseNumber(app.getLicenseNumber());
        response.setBackgroundCheckStatus(app.getBackgroundCheckStatus() != null
                ? app.getBackgroundCheckStatus().name() : null);
        response.setAdminNotes(app.getAdminNotes());
        response.setCreatedAt(app.getCreatedAt());
        response.setUpdatedAt(app.getUpdatedAt());
        return response;
    }
}
