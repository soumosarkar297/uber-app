package com.rideshare.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing a user's wallet details.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    @Schema(description = "Unique identifier of the wallet", example = "wallet-abc-123")
    private String id;

    @Schema(description = "Unique identifier of the wallet owner", example = "user-123")
    private String userId;

    @Schema(description = "Type of the wallet owner (RIDER or DRIVER)", example = "RIDER")
    private String userType;

    @Schema(description = "Current available balance in the wallet", example = "2500.00")
    private BigDecimal balance;

    @Schema(description = "Total amount ever added to the wallet", example = "5000.00")
    private BigDecimal totalAdded;

    @Schema(description = "Total amount ever spent from the wallet", example = "2500.00")
    private BigDecimal totalSpent;

    @Schema(description = "Whether the wallet is currently active")
    private boolean active;

    @Schema(description = "Timestamp when the wallet was created")
    private LocalDateTime createdAt;
}
