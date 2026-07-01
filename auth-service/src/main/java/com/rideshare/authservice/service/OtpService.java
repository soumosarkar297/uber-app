package com.rideshare.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Service for OTP (One-Time Password) generation, storage, verification, and rate limiting.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class OtpService {

    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String ATTEMPTS_KEY_PREFIX = "otp:attempts:";
    private static final String RATE_LIMIT_KEY_PREFIX = "otp:rate-limit:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final int otpLength;
    private final int ttlMinutes;
    private final int maxAttempts;
    private final int rateLimitWindowMinutes;
    private final int rateLimitMaxRequests;
    private final Random random;

    public OtpService(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${otp.length:6}") int otpLength,
            @Value("${otp.ttl-minutes:5}") int ttlMinutes,
            @Value("${otp.max-attempts:3}") int maxAttempts,
            @Value("${otp.rate-limit-window-minutes:15}") int rateLimitWindowMinutes,
            @Value("${otp.rate-limit-max-requests:5}") int rateLimitMaxRequests) {
        this.redisTemplate = redisTemplate;
        this.otpLength = otpLength;
        this.ttlMinutes = ttlMinutes;
        this.maxAttempts = maxAttempts;
        this.rateLimitWindowMinutes = rateLimitWindowMinutes;
        this.rateLimitMaxRequests = rateLimitMaxRequests;
        this.random = new Random();
    }

    /**
     * Generates a 6-digit numeric OTP and stores it in Redis with TTL.
     *
     * @param phoneNumber the phone number to generate OTP for
     * @return the generated OTP as a string
     */
    public String generateAndStoreOtp(String phoneNumber) {
        String otp = generateNumericOtp(otpLength);
        String otpKey = OTP_KEY_PREFIX + phoneNumber;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + phoneNumber;

        redisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(ttlMinutes));
        redisTemplate.delete(attemptsKey);

        return otp;
    }

    /**
     * Verifies the provided OTP against the stored value.
     * Tracks failed attempts and enforces max attempts limit.
     * Deletes OTP on successful verification.
     *
     * @param phoneNumber the phone number associated with the OTP
     * @param otp the OTP to verify
     * @return true if OTP is valid, false otherwise
     * @throws IllegalStateException if max attempts exceeded
     */
    public boolean verifyOtp(String phoneNumber, String otp) {
        String otpKey = OTP_KEY_PREFIX + phoneNumber;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + phoneNumber;

        Object storedOtpObj = redisTemplate.opsForValue().get(otpKey);
        if (storedOtpObj == null) {
            incrementAttempts(phoneNumber);
            return false;
        }

        String storedOtp = storedOtpObj.toString();
        if (!storedOtp.equals(otp)) {
            incrementAttempts(phoneNumber);
            return false;
        }

        cleanup(phoneNumber);
        return true;
    }

    /**
     * Checks if the phone number has exceeded the rate limit.
     *
     * @param phoneNumber the phone number to check
     * @return true if rate limited, false otherwise
     */
    public boolean isRateLimited(String phoneNumber) {
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + phoneNumber;
        Object countObj = redisTemplate.opsForValue().get(rateLimitKey);
        if (countObj == null) {
            return false;
        }
        long count = Long.parseLong(countObj.toString());
        return count >= rateLimitMaxRequests;
    }

    /**
     * Increments the rate limit counter for the phone number.
     * Sets expiration on first request.
     *
     * @param phoneNumber the phone number to increment rate limit for
     */
    public void incrementRateLimit(String phoneNumber) {
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + phoneNumber;
        Long count = redisTemplate.opsForValue().increment(rateLimitKey);
        if (count != null && count == 1) {
            redisTemplate.expire(rateLimitKey, Duration.ofMinutes(rateLimitWindowMinutes));
        }
    }

    /**
     * Increments the failed attempts counter for the phone number.
     * Throws exception if max attempts exceeded.
     *
     * @param phoneNumber the phone number to increment attempts for
     * @throws IllegalStateException if max attempts exceeded
     */
    public void incrementAttempts(String phoneNumber) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + phoneNumber;
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, Duration.ofMinutes(ttlMinutes));
        }
        if (attempts != null && attempts >= maxAttempts) {
            cleanup(phoneNumber);
            throw new IllegalStateException("Maximum OTP verification attempts exceeded for phone number: " + phoneNumber);
        }
    }

    /**
     * Cleans up all OTP-related data for the phone number.
     * Deletes OTP, attempts counter, and rate limit counter.
     *
     * @param phoneNumber the phone number to clean up data for
     */
    public void cleanup(String phoneNumber) {
        String otpKey = OTP_KEY_PREFIX + phoneNumber;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + phoneNumber;
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + phoneNumber;

        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptsKey);
        redisTemplate.delete(rateLimitKey);
    }

    /**
     * Generates a numeric OTP of the specified length.
     *
     * @param length the length of the OTP
     * @return the generated OTP as a string
     */
    private String generateNumericOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}