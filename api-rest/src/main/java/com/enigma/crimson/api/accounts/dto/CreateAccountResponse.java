package com.enigma.crimson.api.accounts.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateAccountResponse(
        UUID accountId,
        UUID customerId,
        String currency,
        BigDecimal balance,
        String status,
        OffsetDateTime createdAt
) {
}
