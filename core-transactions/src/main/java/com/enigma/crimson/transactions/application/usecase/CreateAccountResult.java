package com.enigma.crimson.transactions.application.usecase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateAccountResult(
        UUID accountId,
        UUID customerId,
        String currency,
        BigDecimal balance,
        String status,
        OffsetDateTime createdAt
) {
}
