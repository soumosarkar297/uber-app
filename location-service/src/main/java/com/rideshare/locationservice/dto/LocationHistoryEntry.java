package com.rideshare.locationservice.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "A single recorded location point in a driver's history")
public class LocationHistoryEntry {

    @Schema(description = "Latitude coordinate of the recorded point", example = "28.6139")
    private double latitude;

    @Schema(description = "Longitude coordinate of the recorded point", example = "77.2090")
    private double longitude;

    @Schema(description = "Timestamp when the location was recorded", example = "2026-07-02T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Heading in degrees (0-360) at the recorded point", example = "270.0")
    private Double heading;

    @Schema(description = "Speed in km/h at the recorded point", example = "40.0")
    private Double speed;
}
