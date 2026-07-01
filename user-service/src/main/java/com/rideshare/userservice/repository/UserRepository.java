package com.rideshare.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rideshare.userservice.entity.Driver;
import com.rideshare.userservice.entity.User;
import com.rideshare.userservice.entity.UserType;
import com.rideshare.userservice.entity.VerificationStatus;

/**
 * Repository for User entity with custom query methods.
 * Supports both Rider and Driver entities through single-table inheritance.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    List<User> findByUserType(UserType userType);

    List<User> findByVerificationStatus(VerificationStatus verificationStatus);

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.userType = :userType")
    Optional<User> findByPhoneNumberAndUserType(@Param("phoneNumber") String phoneNumber, @Param("userType") UserType userType);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.userType = :userType")
    Optional<User> findByEmailAndUserType(@Param("email") String email, @Param("userType") UserType userType);

    long countByUserType(UserType userType);

    long countByVerificationStatus(VerificationStatus verificationStatus);

    // Driver-specific queries
    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByVehicleNumber(String vehicleNumber);

    @Query("SELECT u FROM User u WHERE u.licenseNumber = :licenseNumber AND u.userType = 'DRIVER'")
    Optional<User> findByLicenseNumber(@Param("licenseNumber") String licenseNumber);

    @Query("SELECT u FROM User u WHERE u.userType = 'DRIVER' AND u.isAvailable = true AND u.isOnline = true AND u.verificationStatus = 'VERIFIED'")
    List<Driver> findAvailableDrivers();
}