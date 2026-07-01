package com.rideshare.notificationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends email notifications using Spring Mail.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    /**
     * Sends a plain text email.
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent to: {} subject: {}", to, subject);
            return true;

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Sends a ride confirmation email.
     */
    public void sendRideConfirmationEmail(String to, String rideId, String pickupAddress,
                                           String dropAddress, double fare) {
        String subject = "Ride Confirmation - " + rideId;
        String body = String.format(
                "Your ride has been confirmed!\n\n" +
                "Ride ID: %s\nPickup: %s\nDrop: %s\nEstimated Fare: ₹%.2f\n\n" +
                "Thank you for riding with us!",
                rideId, pickupAddress, dropAddress, fare);
        sendEmail(to, subject, body);
    }

    /**
     * Sends a ride receipt email.
     */
    public void sendRideReceiptEmail(String to, String rideId, String pickupAddress,
                                      String dropAddress, double fare, double distanceKm) {
        String subject = "Ride Receipt - " + rideId;
        String body = String.format(
                "Your ride receipt!\n\n" +
                "Ride ID: %s\nPickup: %s\nDrop: %s\nDistance: %.1f km\nTotal Fare: ₹%.2f\n\n" +
                "Thank you for riding with us!",
                rideId, pickupAddress, dropAddress, distanceKm, fare);
        sendEmail(to, subject, body);
    }
}
