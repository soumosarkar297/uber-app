package com.rideshare.locationservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single recorded location point in a driver's history.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationHistoryEntry {

    private double latitude;

    private double longitude;

    private LocalDateTime timestamp;

    private Double heading;

    private Double speed;
}
