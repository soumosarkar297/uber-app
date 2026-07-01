package com.rideshare.driveronboardingservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.driveronboardingservice.entity.Vehicle;

/**
 * Repository for accessing vehicle persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    Optional<Vehicle> findByDriverId(String driverId);
}
