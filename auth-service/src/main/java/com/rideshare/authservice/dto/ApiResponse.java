package com.rideshare.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Generic API response wrapper.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    @Schema(
        description = "Indicates whether the request was successful",
        example = "true"
    )
    private boolean success;

    @Schema(
        description = "Human-readable message describing the result",
        example = "Success"
    )
    private String message;

    @Schema(
        description = "Response payload, may be null on error"
    )
    private T data;

    @Schema(
        description = "Machine-readable error code, null on success",
        example = "OTP_EXPIRED"
    )
    private String errorCode;

    @Schema(
        description = "ISO-8601 timestamp of when the response was generated",
        example = "2026-07-02T12:00:00Z"
    )
    private Instant timestamp;

    public ApiResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = Instant.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, "ERROR");
    }
}
