package com.rideshare.pricingservice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.pricingservice.dto.ApplyPromoRequest;
import com.rideshare.pricingservice.dto.ApplyPromoResponse;
import com.rideshare.pricingservice.dto.BaseFareRequest;
import com.rideshare.pricingservice.dto.BaseFareResponse;
import com.rideshare.pricingservice.dto.FareEstimationRequest;
import com.rideshare.pricingservice.dto.FareEstimationResponse;
import com.rideshare.pricingservice.dto.PromoCodeValidationRequest;
import com.rideshare.pricingservice.dto.PromoCodeValidationResponse;
import com.rideshare.pricingservice.dto.SurgePricingRequest;
import com.rideshare.pricingservice.dto.SurgePricingResponse;
import com.rideshare.pricingservice.entity.PromoCode;
import com.rideshare.pricingservice.service.BaseFareService;
import com.rideshare.pricingservice.service.PromoCodeService;
import com.rideshare.pricingservice.service.PricingService;
import com.rideshare.pricingservice.service.SurgePricingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Exposes REST endpoints for fare estimation, surge pricing, and promo codes.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/pricing")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Pricing Service", description = "Dynamic pricing, surge, promo codes, and fare estimation")
public class PricingController {

    private final PricingService pricingService;
    private final BaseFareService baseFareService;
    private final SurgePricingService surgePricingService;
    private final PromoCodeService promoCodeService;

    // ── Fare Estimation ──

    @PostMapping("/estimate")
    @Operation(summary = "Estimate Fare", description = "Calculates complete fare with base, distance, time, surge, and promo")
    public ResponseEntity<FareEstimationResponse> estimateFare(
            @Valid @RequestBody FareEstimationRequest request) {
        log.info("Fare estimation request for {} trip", request.getVehicleType());
        return ResponseEntity.ok(pricingService.estimateFare(request));
    }

    // ── Base Fare ──

    @PostMapping("/base-fare")
    @Operation(summary = "Get Base Fare", description = "Gets base fare configuration for a vehicle type and city")
    public ResponseEntity<BaseFareResponse> getBaseFare(
            @Valid @RequestBody BaseFareRequest request) {
        return ResponseEntity.ok(baseFareService.getBaseFare(request));
    }

    // ── Surge Pricing ──

    @PostMapping("/surge")
    @Operation(summary = "Calculate Surge", description = "Calculates surge multiplier for a zone based on demand/supply")
    public ResponseEntity<SurgePricingResponse> calculateSurge(
            @RequestBody SurgePricingRequest request) {
        return ResponseEntity.ok(surgePricingService.calculateSurge(request));
    }

    @GetMapping("/surge/status")
    @Operation(summary = "Check Surge Status", description = "Checks if surge pricing is active for a zone")
    public ResponseEntity<SurgePricingResponse> checkSurgeStatus(
            @RequestParam String zone) {
        double multiplier = surgePricingService.getCurrentSurgeMultiplier(zone);
        String status = multiplier > 1.0 ? "SURGE_ACTIVE" : "NORMAL";
        return ResponseEntity.ok(new SurgePricingResponse(
                zone, multiplier, 0, 0, BigDecimal.ZERO, status));
    }

    // ── Promo Codes ──

    @PostMapping("/promo/validate")
    @Operation(summary = "Validate Promo Code", description = "Validates a promo code for a user and order value")
    public ResponseEntity<PromoCodeValidationResponse> validatePromoCode(
            @Valid @RequestBody PromoCodeValidationRequest request) {
        return ResponseEntity.ok(promoCodeService.validatePromoCode(request));
    }

    @PostMapping("/promo/apply")
    @Operation(summary = "Apply Promo Code", description = "Applies a promo code and returns discounted amount")
    public ResponseEntity<ApplyPromoResponse> applyPromoCode(
            @RequestBody ApplyPromoRequest request) {
        return ResponseEntity.ok(promoCodeService.applyPromoCode(request));
    }

    @PostMapping("/promo/create")
    @Operation(summary = "Create Promo Code", description = "Creates a new promo code")
    public ResponseEntity<PromoCode> createPromoCode(
            @RequestBody PromoCode promoCode) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(promoCodeService.createPromoCode(promoCode));
    }
}
