package com.rideshare.driverservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rideshare.driverservice.dto.DocumentUploadRequest;
import com.rideshare.driverservice.dto.DriverProfileResponse;
import com.rideshare.driverservice.dto.DriverProfileUpdateRequest;
import com.rideshare.driverservice.dto.DriverRegistrationRequest;
import com.rideshare.driverservice.dto.RegistrationResponse;
import com.rideshare.driverservice.entity.DocumentType;
import com.rideshare.driverservice.entity.Driver;
import com.rideshare.driverservice.entity.VerificationStatus;
import com.rideshare.driverservice.entity.VehicleType;
import com.rideshare.driverservice.event.DriverRegisteredEvent;
import com.rideshare.driverservice.event.UserRegisteredEvent;
import com.rideshare.driverservice.repository.DriverRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for driver-specific operations. Handles driver registration with
 * vehicle info, profile management, and document handling.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final DocumentService documentService;
    private final DriverEventPublisher eventPublisher;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Register a new driver. Creates Driver entity and initial documents.
     */
    @Transactional
    public RegistrationResponse registerDriver(DriverRegistrationRequest request) {
        // Validate phone number and email don't exist
        if (driverRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        // Check license number uniqueness
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new IllegalArgumentException("License number already registered");
        }
        // Check vehicle number uniqueness
        if (driverRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new IllegalArgumentException("Vehicle number already registered");
        }

        // Create Driver entity
        Driver driver = new Driver();
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setEmail(request.getEmail());
        driver.setProfileImageUrl(request.getProfileImageUrl());
        driver.setVerificationStatus(VerificationStatus.PENDING);

        // Driver-specific fields
        driver.setLicenseNumber(request.getLicenseNumber());
        if (request.getLicenseExpiryDate() != null && !request.getLicenseExpiryDate().isBlank()) {
            driver.setLicenseExpiryDate(LocalDate.parse(request.getLicenseExpiryDate(), DATE_FORMATTER));
        }
        driver.setVehicleNumber(request.getVehicleNumber());
        driver.setVehicleModel(request.getVehicleModel());
        driver.setVehicleColor(request.getVehicleColor());
        driver.setVehicleYear(request.getVehicleYear());
        if (request.getVehicleType() != null && !request.getVehicleType().isBlank()) {
            driver.setVehicleType(VehicleType.valueOf(request.getVehicleType().toUpperCase()));
        } else {
            driver.setVehicleType(VehicleType.SEDAN);
        }
        driver.setIsAvailable(false);
        driver.setIsOnline(false);
        driver.setTotalTrips(0);
        driver.setRating(5.0);
        driver.setEarnings(BigDecimal.ZERO);

        Driver savedDriver = driverRepository.save(driver);

        // Publish user registered event
        UserRegisteredEvent userEvent = UserRegisteredEvent.builder()
                .userId(savedDriver.getId())
                .phoneNumber(savedDriver.getPhoneNumber())
                .userType("DRIVER")
                .timestamp(Instant.now())
                .build();
        eventPublisher.publishUserRegistered(userEvent);

        // Publish driver registered event with vehicle info
        DriverRegisteredEvent driverEvent = DriverRegisteredEvent.builder()
                .userId(savedDriver.getId())
                .vehicleNumber(savedDriver.getVehicleNumber())
                .vehicleModel(savedDriver.getVehicleModel())
                .vehicleColor(savedDriver.getVehicleColor())
                .vehicleYear(savedDriver.getVehicleYear())
                .vehicleType(savedDriver.getVehicleType() != null ? savedDriver.getVehicleType().name() : null)
                .licenseNumber(savedDriver.getLicenseNumber())
                .timestamp(Instant.now())
                .build();
        eventPublisher.publishDriverRegistered(driverEvent);

        // Create initial documents if file URLs provided
        if (request.getLicenseFileUrl() != null && !request.getLicenseFileUrl().isBlank()) {
            createInitialDocument(savedDriver.getId(), DocumentType.DRIVING_LICENSE, request.getLicenseFileUrl(), "driving_license.pdf");
        }
        if (request.getVehicleRegistrationFileUrl() != null && !request.getVehicleRegistrationFileUrl().isBlank()) {
            createInitialDocument(savedDriver.getId(), DocumentType.VEHICLE_REGISTRATION, request.getVehicleRegistrationFileUrl(), "vehicle_registration.pdf");
        }
        if (request.getInsuranceFileUrl() != null && !request.getInsuranceFileUrl().isBlank()) {
            createInitialDocument(savedDriver.getId(), DocumentType.VEHICLE_INSURANCE, request.getInsuranceFileUrl(), "insurance.pdf");
        }

        return new RegistrationResponse(
                savedDriver.getId(),
                "DRIVER",
                VerificationStatus.PENDING.name(),
                "Driver registered successfully. Document verification pending."
        );
    }

    /**
     * Create initial document for driver registration.
     */
    private void createInitialDocument(UUID driverId, DocumentType documentType, String fileUrl, String fileName) {
        DocumentUploadRequest docRequest = new DocumentUploadRequest();
        docRequest.setDriverId(driverId);
        docRequest.setDocumentType(documentType.name());
        docRequest.setFileUrl(fileUrl);
        docRequest.setFileName(fileName);
        documentService.uploadDocument(docRequest);
    }

    /**
     * Get driver profile by ID.
     */
    public Optional<DriverProfileResponse> getDriverProfile(UUID driverId) {
        return driverRepository.findById(driverId)
                .map(this::mapToDriverProfileResponse);
    }

    /**
     * Get driver profile by phone number.
     */
    public Optional<DriverProfileResponse> getDriverProfileByPhone(String phoneNumber) {
        return driverRepository.findByPhoneNumber(phoneNumber)
                .map(this::mapToDriverProfileResponse);
    }

    /**
     * Get driver profile by license number.
     */
    public Optional<DriverProfileResponse> getDriverProfileByLicense(String licenseNumber) {
        return driverRepository.findByLicenseNumber(licenseNumber)
                .map(this::mapToDriverProfileResponse);
    }

    /**
     * Update driver profile.
     */
    @Transactional
    public DriverProfileResponse updateDriverProfile(UUID driverId, DriverProfileUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        // Update common fields
        if (request.getFirstName() != null) {
            driver.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            driver.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            if (driverRepository.existsByEmail(request.getEmail()) && !driver.getEmail().equals(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            driver.setEmail(request.getEmail());
        }
        if (request.getProfileImageUrl() != null) {
            driver.setProfileImageUrl(request.getProfileImageUrl());
        }

        // Update driver-specific fields
        if (request.getLicenseNumber() != null) {
            if (driverRepository.existsByLicenseNumber(request.getLicenseNumber()) && !driver.getLicenseNumber().equals(request.getLicenseNumber())) {
                throw new IllegalArgumentException("License number already in use");
            }
            driver.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getLicenseExpiryDate() != null && !request.getLicenseExpiryDate().isBlank()) {
            driver.setLicenseExpiryDate(LocalDate.parse(request.getLicenseExpiryDate(), DATE_FORMATTER));
        }
        if (request.getVehicleNumber() != null) {
            if (driverRepository.existsByVehicleNumber(request.getVehicleNumber()) && !driver.getVehicleNumber().equals(request.getVehicleNumber())) {
                throw new IllegalArgumentException("Vehicle number already in use");
            }
            driver.setVehicleNumber(request.getVehicleNumber());
        }
        if (request.getVehicleModel() != null) {
            driver.setVehicleModel(request.getVehicleModel());
        }
        if (request.getVehicleColor() != null) {
            driver.setVehicleColor(request.getVehicleColor());
        }
        if (request.getVehicleYear() != null) {
            driver.setVehicleYear(request.getVehicleYear());
        }
        if (request.getVehicleType() != null && !request.getVehicleType().isBlank()) {
            driver.setVehicleType(VehicleType.valueOf(request.getVehicleType().toUpperCase()));
        }
        if (request.getIsAvailable() != null) {
            driver.setIsAvailable(request.getIsAvailable());
        }
        if (request.getIsOnline() != null) {
            driver.setIsOnline(request.getIsOnline());
        }

        Driver updatedDriver = driverRepository.save(driver);
        return mapToDriverProfileResponse(updatedDriver);
    }

    /**
     * Update driver verification status.
     */
    @Transactional
    public Driver updateVerificationStatus(UUID driverId, VerificationStatus status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        driver.setVerificationStatus(status);
        return driverRepository.save(driver);
    }

    /**
     * Set driver online/offline status.
     */
    @Transactional
    public void setOnlineStatus(UUID driverId, boolean isOnline) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        driver.setIsOnline(isOnline);
        if (!isOnline) {
            driver.setIsAvailable(false);
        }
        driverRepository.save(driver);
    }

    /**
     * Set driver availability.
     */
    @Transactional
    public void setAvailability(UUID driverId, boolean isAvailable) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        driver.setIsAvailable(isAvailable);
        driverRepository.save(driver);
    }

    /**
     * Update driver location.
     */
    @Transactional
    public void updateLocation(UUID driverId, Double latitude, Double longitude) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        driver.setCurrentLatitude(latitude);
        driver.setCurrentLongitude(longitude);
        driver.setLastLocationUpdate(LocalDateTime.now());
        driverRepository.save(driver);
    }

    /**
     * Increment total trips for a driver.
     */
    @Transactional
    public void incrementTotalTrips(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        driver.setTotalTrips(driver.getTotalTrips() + 1);
        driverRepository.save(driver);
    }

    /**
     * Add to driver earnings.
     */
    @Transactional
    public void addToEarnings(UUID driverId, BigDecimal amount) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        driver.setEarnings(driver.getEarnings().add(amount));
        driverRepository.save(driver);
    }

    /**
     * Update driver rating.
     */
    @Transactional
    public void updateRating(UUID driverId, Double newRating) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        driver.setRating(newRating);
        driverRepository.save(driver);
    }

    /**
     * Get available drivers near a location (simplified - returns all available
     * drivers).
     */
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }

    /**
     * Check if driver exists and is verified.
     */
    public boolean isDriverVerified(UUID driverId) {
        return driverRepository.findById(driverId)
                .map(driver -> driver.getVerificationStatus() == VerificationStatus.VERIFIED)
                .orElse(false);
    }

    /**
     * Check if driver is online and available.
     */
    public boolean isDriverOnlineAndAvailable(UUID driverId) {
        return driverRepository.findById(driverId)
                .map(driver -> driver.getIsOnline() && driver.getIsAvailable())
                .orElse(false);
    }

    /**
     * Map Driver entity to DriverProfileResponse DTO.
     */
    private DriverProfileResponse mapToDriverProfileResponse(Driver driver) {
        DriverProfileResponse response = new DriverProfileResponse();
        response.setId(driver.getId());
        response.setFirstName(driver.getFirstName());
        response.setLastName(driver.getLastName());
        response.setPhoneNumber(driver.getPhoneNumber());
        response.setEmail(driver.getEmail());
        response.setProfileImageUrl(driver.getProfileImageUrl());
        response.setVerificationStatus(driver.getVerificationStatus().name());
        response.setCreatedAt(driver.getCreatedAt());
        response.setUpdatedAt(driver.getUpdatedAt());
        response.setLicenseNumber(driver.getLicenseNumber());
        response.setLicenseExpiryDate(driver.getLicenseExpiryDate());
        response.setVehicleNumber(driver.getVehicleNumber());
        response.setVehicleModel(driver.getVehicleModel());
        response.setVehicleColor(driver.getVehicleColor());
        response.setVehicleYear(driver.getVehicleYear());
        response.setVehicleType(driver.getVehicleType() != null ? driver.getVehicleType().name() : null);
        response.setIsAvailable(driver.getIsAvailable());
        response.setIsOnline(driver.getIsOnline());
        response.setTotalTrips(driver.getTotalTrips());
        response.setRating(driver.getRating());
        response.setEarnings(driver.getEarnings());
        response.setCurrentLatitude(driver.getCurrentLatitude());
        response.setCurrentLongitude(driver.getCurrentLongitude());
        response.setLastLocationUpdate(driver.getLastLocationUpdate());
        return response;
    }
}
