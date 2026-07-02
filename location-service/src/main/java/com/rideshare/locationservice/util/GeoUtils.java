package com.rideshare.locationservice.util;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

/**
 * Shared geo-spatial utility methods for distance and bearing calculations.
 * Single source of truth for the Haversine formula across the system.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public final class GeoUtils {

    public static final double EARTH_RADIUS_KM = 6371.0;

    private GeoUtils() {
    }

    /**
     * Calculates the great-circle distance between two points using the Haversine formula.
     *
     * @param lat1 latitude of point 1 in degrees
     * @param lon1 longitude of point 1 in degrees
     * @param lat2 latitude of point 2 in degrees
     * @param lon2 longitude of point 2 in degrees
     * @return distance in kilometers
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the initial bearing from point 1 to point 2.
     *
     * @param lat1 latitude of point 1 in degrees
     * @param lon1 longitude of point 1 in degrees
     * @param lat2 latitude of point 2 in degrees
     * @param lon2 longitude of point 2 in degrees
     * @return bearing in degrees (0-360)
     */
    public static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double dLon = Math.toRadians(lon2 - lon1);

        double y = Math.sin(dLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad)
                - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    /**
     * Calculates estimated travel time in minutes assuming a given average speed.
     *
     * @param distanceKm distance in kilometers
     * @param avgSpeedKmh average speed in km/h
     * @return estimated time in minutes
     */
    public static double estimateTimeMinutes(double distanceKm, double avgSpeedKmh) {
        if (avgSpeedKmh <= 0) return 0;
        return (distanceKm / avgSpeedKmh) * 60.0;
    }

    /**
     * Rounds a value to the specified number of decimal places.
     */
    public static double round(double value, int decimalPlaces) {
        double factor = Math.pow(10, decimalPlaces);
        return Math.round(value * factor) / factor;
    }

    /**
     * Creates a Spring Data Point from latitude/longitude (note: Point takes lon, lat order).
     */
    public static Point toPoint(double latitude, double longitude) {
        return new Point(longitude, latitude);
    }

    /**
     * Creates a Spring Data Distance in kilometers.
     */
    public static Distance distanceKm(double km) {
        return new Distance(km, Metrics.KILOMETERS);
    }
}
