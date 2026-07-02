package com.rideshare.pricingservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.rideshare.pricingservice.dto.ApplyPromoRequest;
import com.rideshare.pricingservice.dto.ApplyPromoResponse;
import com.rideshare.pricingservice.dto.BaseFareRequest;
import com.rideshare.pricingservice.dto.BaseFareResponse;
import com.rideshare.pricingservice.dto.FareEstimationRequest;
import com.rideshare.pricingservice.dto.FareEstimationResponse;
import com.rideshare.pricingservice.dto.SurgePricingRequest;
import com.rideshare.pricingservice.util.GeoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates fare estimation by combining base fare, surge, and promo codes.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PricingService {

    private final BaseFareService baseFareService;
    private final SurgePricingService surgePricingService;
    private final PromoCodeService promoCodeService;

    private static final double AVG_SPEED_KMH = 30.0;

    /**
     * Calculates a complete fare estimation for a ride request.
     * Combines base fare, distance/time pricing, surge, and promo codes.
     */
    public FareEstimationResponse estimateFare(FareEstimationRequest request) {
        log.info("Estimating fare for {} from ({},{}) to ({},{})",
                request.getVehicleType(),
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDropLatitude(), request.getDropLongitude());

        // Get base fare configuration
        BaseFareRequest baseFareRequest = new BaseFareRequest(
                request.getVehicleType(), request.getCity() != null ? request.getCity() : "default");
        BaseFareResponse baseFare = baseFareService.getBaseFare(baseFareRequest);

        // Calculate distance and duration
        double distanceKm = GeoUtils.haversine(
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDropLatitude(), request.getDropLongitude());
        double durationMinutes = (distanceKm / AVG_SPEED_KMH) * 60;

        // Distance fare
        BigDecimal distanceFare = baseFare.getPerKmRate()
                .multiply(BigDecimal.valueOf(distanceKm))
                .setScale(2, RoundingMode.HALF_UP);

        // Time fare
        BigDecimal timeFare = baseFare.getPerMinuteRate()
                .multiply(BigDecimal.valueOf(durationMinutes))
                .setScale(2, RoundingMode.HALF_UP);

        // Subtotal before surge
        BigDecimal subtotal = baseFare.getBaseFare()
                .add(distanceFare)
                .add(timeFare)
                .add(baseFare.getBookingFee());

        // Apply surge pricing
        String zone = request.getCity() != null ? request.getCity() : "default";
        SurgePricingRequest surgeRequest = new SurgePricingRequest(
                zone, request.getPickupLatitude(), request.getPickupLongitude(), 2.0);
        var surgeResponse = surgePricingService.calculateSurge(surgeRequest);

        BigDecimal surgeMultiplier = BigDecimal.valueOf(surgeResponse.getSurgeMultiplier());
        BigDecimal surgeAmount = subtotal.multiply(surgeMultiplier.subtract(BigDecimal.ONE))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal afterSurge = subtotal.add(surgeAmount);

        // Apply promo code
        BigDecimal discount = BigDecimal.ZERO;
        String promoDescription = null;
        if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
            var promoResult = promoCodeService.applyPromoCode(
                    new ApplyPromoRequest(request.getPromoCode(), afterSurge));
            if (promoResult.isApplied()) {
                discount = promoResult.getDiscountAmount();
                promoDescription = promoResult.getDescription();
            }
        }

        BigDecimal totalFare = afterSurge.subtract(discount).max(baseFare.getMinimumFare());

        return new FareEstimationResponse(
                request.getVehicleType(),
                baseFare.getBaseFare(),
                distanceFare,
                timeFare,
                baseFare.getBookingFee(),
                subtotal,
                surgeMultiplier,
                surgeAmount,
                afterSurge,
                discount,
                totalFare,
                Math.round(distanceKm * 100.0) / 100.0,
                Math.round(durationMinutes * 10.0) / 10.0,
                request.getPromoCode(),
                promoDescription);
    }
}
