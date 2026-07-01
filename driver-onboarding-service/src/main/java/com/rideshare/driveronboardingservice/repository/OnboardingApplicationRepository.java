package com.rideshare.driveronboardingservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.driveronboardingservice.entity.OnboardingApplication;

/**
 * Repository for accessing onboarding application persistence.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public interface OnboardingApplicationRepository extends JpaRepository<OnboardingApplication, String> {

    Optional<OnboardingApplication> findByDriverId(String driverId);

    List<OnboardingApplication> findByStatus(OnboardingApplication.ApplicationStatus status);
}
