package com.rideshare.locationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideshare.locationservice.dto.DirectionRequest;
import com.rideshare.locationservice.dto.DirectionResponse;
import com.rideshare.locationservice.dto.DistanceMatrixRequest;
import com.rideshare.locationservice.dto.DistanceMatrixResponse;
import com.rideshare.locationservice.dto.EtaResponse;
import com.rideshare.locationservice.util.GeoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Integrates with Google Maps APIs for directions, distance matrix, and ETA
 * calculations. Falls back to Haversine-based estimation when API is
 * unavailable.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleMapsService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.maps.api.key:}")
    private String apiKey;

    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final double FALLBACK_AVG_SPEED_KMH = 30.0;
    private static final long CACHE_TTL_MS = 300_000; // 5 minutes

    private final Map<String, CacheEntry<DirectionResponse>> directionsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<DistanceMatrixResponse>> matrixCache = new ConcurrentHashMap<>();

    private record CacheEntry<T>(T value, long timestamp) {
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }

    /**
     * Gets directions between two points using Google Maps Direction API. Falls
     * back to haversine estimation if API key is not configured.
     */
    public DirectionResponse getDirections(DirectionRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            return getFallbackDirections(request);
        }

        // Check cache
        String cacheKey = String.format("%.6f,%.6f->%.6f,%.6f",
                request.getOriginLatitude(), request.getOriginLongitude(),
                request.getDestinationLatitude(), request.getDestinationLongitude());
        CacheEntry<DirectionResponse> cached = directionsCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.value();
        }

        try {
            String origin = request.getOriginLatitude() + "," + request.getOriginLongitude();
            String destination = request.getDestinationLatitude() + ","
                    + request.getDestinationLongitude();

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(DIRECTIONS_URL)
                    .queryParam("origin", origin)
                    .queryParam("destination", destination)
                    .queryParam("key", apiKey)
                    .queryParam("mode", "driving");

            ResponseEntity<String> apiResponse = restTemplate.getForEntity(
                    builder.toUriString(), String.class);

            JsonNode root = objectMapper.readTree(apiResponse.getBody());
            String status = root.path("status").asText();

            if (!"OK".equals(status)) {
                log.warn("Google Directions API returned status: {}", status);
                return getFallbackDirections(request);
            }

            JsonNode route = root.path("routes").get(0);
            JsonNode leg = route.path("legs").get(0);

            double distanceMeters = leg.path("distance").path("value").asDouble();
            double durationSeconds = leg.path("duration").path("value").asDouble();
            String polyline = route.path("overview_polyline").path("points").asText();

            List<DirectionResponse.Step> steps = new ArrayList<>();
            for (JsonNode stepNode : leg.path("steps")) {
                steps.add(new DirectionResponse.Step(
                        stepNode.path("html_instructions").asText(""),
                        stepNode.path("distance").path("value").asDouble() / 1000.0,
                        stepNode.path("duration").path("value").asDouble() / 60.0,
                        stepNode.path("polyline").path("points").asText(""),
                        stepNode.path("start_location").path("lat").asDouble(),
                        stepNode.path("start_location").path("lng").asDouble(),
                        stepNode.path("end_location").path("lat").asDouble(),
                        stepNode.path("end_location").path("lng").asDouble()));
            }

            DirectionResponse response = new DirectionResponse(
                    distanceMeters / 1000.0,
                    durationSeconds / 60.0,
                    polyline,
                    steps,
                    "OK");

            directionsCache.put(cacheKey, new CacheEntry<>(response, System.currentTimeMillis()));
            return response;

        } catch (Exception e) {
            log.error("Error calling Google Directions API: {}", e.getMessage());
            return getFallbackDirections(request);
        }
    }

    /**
     * Gets distance and duration matrix between multiple origins and
     * destinations. Falls back to haversine estimation if API key is not
     * configured.
     */
    public DistanceMatrixResponse getDistanceMatrix(DistanceMatrixRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            return getFallbackDistanceMatrix(request);
        }

        // Check cache
        String cacheKey = request.getOrigins().toString() + "->" + request.getDestinations().toString();
        CacheEntry<DistanceMatrixResponse> cached = matrixCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.value();
        }

        try {
            StringBuilder origins = new StringBuilder();
            for (DistanceMatrixRequest.LocationPoint origin : request.getOrigins()) {
                if (!origins.isEmpty()) {
                    origins.append("|");
                }
                origins.append(origin.getLatitude()).append(",").append(origin.getLongitude());
            }

            StringBuilder destinations = new StringBuilder();
            for (DistanceMatrixRequest.LocationPoint dest : request.getDestinations()) {
                if (!destinations.isEmpty()) {
                    destinations.append("|");
                }
                destinations.append(dest.getLatitude()).append(",").append(dest.getLongitude());
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(DISTANCE_MATRIX_URL)
                    .queryParam("origins", origins)
                    .queryParam("destinations", destinations)
                    .queryParam("key", apiKey)
                    .queryParam("mode", "driving");

            ResponseEntity<String> apiResponse = restTemplate.getForEntity(
                    builder.toUriString(), String.class);

            JsonNode root = objectMapper.readTree(apiResponse.getBody());
            String status = root.path("status").asText();

            List<List<DistanceMatrixResponse.Element>> rows = new ArrayList<>();
            for (JsonNode rowNode : root.path("rows")) {
                List<DistanceMatrixResponse.Element> row = new ArrayList<>();
                for (JsonNode elementNode : rowNode.path("elements")) {
                    row.add(new DistanceMatrixResponse.Element(
                            new DistanceMatrixResponse.DistanceDistance(
                                    elementNode.path("distance").path("text").asText(""),
                                    elementNode.path("distance").path("value").asDouble() / 1000.0),
                            new DistanceMatrixResponse.DistanceDuration(
                                    elementNode.path("duration").path("text").asText(""),
                                    elementNode.path("duration").path("value").asDouble() / 60.0),
                            elementNode.path("status").asText("OK")));
                }
                rows.add(row);
            }

            DistanceMatrixResponse response = new DistanceMatrixResponse(rows, status);
            matrixCache.put(cacheKey, new CacheEntry<>(response, System.currentTimeMillis()));
            return response;

        } catch (Exception e) {
            log.error("Error calling Google Distance Matrix API: {}", e.getMessage());
            return getFallbackDistanceMatrix(request);
        }
    }

    /**
     * Calculates ETA using Google Maps Direction API, falling back to
     * haversine.
     */
    public EtaResponse calculateEta(String driverId,
            double driverLat, double driverLon,
            double pickupLat, double pickupLon,
            double dropLat, double dropLon) {
        DirectionRequest directionRequest = new DirectionRequest(
                driverLat, driverLon, pickupLat, pickupLon, null);
        DirectionResponse direction = getDirections(directionRequest);

        double pickupDistance = direction.getDistanceKm();
        double pickupEta = direction.getDurationMinutes();

        DirectionRequest tripRequest = new DirectionRequest(
                pickupLat, pickupLon, dropLat, dropLon, null);
        DirectionResponse tripDirection = getDirections(tripRequest);

        double tripDistance = tripDirection.getDistanceKm();
        double tripDuration = tripDirection.getDurationMinutes();

        EtaResponse response = new EtaResponse();
        response.setPickupEtaMinutes(GeoUtils.round(pickupEta, 1));
        response.setTripDurationMinutes(GeoUtils.round(tripDuration, 1));
        response.setTotalDistanceKm(GeoUtils.round(pickupDistance + tripDistance, 2));
        response.setDriverLatitude(driverLat);
        response.setDriverLongitude(driverLon);

        return response;
    }

    /**
     * Decodes an encoded polyline string into a list of lat/lng points. Uses
     * Google's Polyline Encoding Algorithm.
     */
    public List<double[]> decodePolyline(String encoded) {
        List<double[]> points = new ArrayList<>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < encoded.length()) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            points.add(new double[]{lat / 1E5, lng / 1E5});
        }

        return points;
    }

    /**
     * Encodes a list of lat/lng points into a polyline string.
     */
    public String encodePolyline(List<double[]> points) {
        StringBuilder encoded = new StringBuilder();
        int prevLat = 0;
        int prevLng = 0;

        for (double[] point : points) {
            int lat = (int) Math.round(point[0] * 1E5);
            int lng = (int) Math.round(point[1] * 1E5);

            encodeValue(lat - prevLat, encoded);
            encodeValue(lng - prevLng, encoded);

            prevLat = lat;
            prevLng = lng;
        }

        return encoded.toString();
    }

    private void encodeValue(int value, StringBuilder encoded) {
        int shifted = value << 1;
        if (value < 0) {
            shifted = ~shifted;
        }
        while (shifted >= 0x20) {
            encoded.append((char) ((shifted & 0x1f) | 0x20));
            shifted >>= 5;
        }
        encoded.append((char) (shifted | 0x40));
    }

    private DirectionResponse getFallbackDirections(DirectionRequest request) {
        double distance = GeoUtils.haversine(
                request.getOriginLatitude(), request.getOriginLongitude(),
                request.getDestinationLatitude(), request.getDestinationLongitude());
        double duration = GeoUtils.estimateTimeMinutes(distance, FALLBACK_AVG_SPEED_KMH);

        return new DirectionResponse(
                GeoUtils.round(distance, 2),
                GeoUtils.round(duration, 1),
                "",
                List.of(),
                "FALLBACK");
    }

    private DistanceMatrixResponse getFallbackDistanceMatrix(DistanceMatrixRequest request) {
        List<List<DistanceMatrixResponse.Element>> rows = new ArrayList<>();
        for (DistanceMatrixRequest.LocationPoint origin : request.getOrigins()) {
            List<DistanceMatrixResponse.Element> row = new ArrayList<>();
            for (DistanceMatrixRequest.LocationPoint dest : request.getDestinations()) {
                double distance = GeoUtils.haversine(
                        origin.getLatitude(), origin.getLongitude(),
                        dest.getLatitude(), dest.getLongitude());
                double duration = GeoUtils.estimateTimeMinutes(distance, FALLBACK_AVG_SPEED_KMH);
                row.add(new DistanceMatrixResponse.Element(
                        new DistanceMatrixResponse.DistanceDistance(
                                GeoUtils.round(distance, 2) + " km", distance),
                        new DistanceMatrixResponse.DistanceDuration(
                                GeoUtils.round(duration, 1) + " mins", duration),
                        "FALLBACK"));
            }
            rows.add(row);
        }
        return new DistanceMatrixResponse(rows, "FALLBACK");
    }
}
