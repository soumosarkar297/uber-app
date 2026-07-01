package com.rideshare.driverservice.repository;

/**
 * Repository for Driver entity with custom query methods.
 * Manages driver data including availability and verification status.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rideshare.driverservice.entity.Driver;
import com.rideshare.driverservice.entity.VerificationStatus;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Optional<Driver> findByPhoneNumber(String phoneNumber);

    Optional<Driver> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByVehicleNumber(String vehicleNumber);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    List<Driver> findByVerificationStatus(VerificationStatus verificationStatus);

    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.isOnline = true AND d.verificationStatus = 'VERIFIED'")
    List<Driver> findAvailableDrivers();

    long countByVerificationStatus(VerificationStatus verificationStatus);
}
