package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating a new user wallet.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest {

    @NotBlank(message = "User ID is required")
    @Schema(description = "Unique identifier of the user", example = "user-123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotNull(message = "User type is required")
    @Schema(description = "Type of the user (RIDER or DRIVER)", example = "RIDER", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userType;
}
