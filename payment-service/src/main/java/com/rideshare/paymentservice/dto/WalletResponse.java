package com.rideshare.paymentservice.dto;

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

    private String id;
    private String userId;
    private String userType;
    private BigDecimal balance;
    private BigDecimal totalAdded;
    private BigDecimal totalSpent;
    private boolean active;
    private LocalDateTime createdAt;
}
