package com.rideshare.driverservice.context;

import com.rideshare.driverservice.entity.Driver;

/**
 * Provides thread-local storage for the currently authenticated driver.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DriverContext {

    private static final ThreadLocal<Driver> CURRENT_DRIVER = new ThreadLocal<>();

    private DriverContext() {}

    public static Driver getCurrentDriver() {
        return CURRENT_DRIVER.get();
    }

    public static void setCurrentDriver(Driver driver) {
        CURRENT_DRIVER.set(driver);
    }

    public static void clear() {
        CURRENT_DRIVER.remove();
    }
}
