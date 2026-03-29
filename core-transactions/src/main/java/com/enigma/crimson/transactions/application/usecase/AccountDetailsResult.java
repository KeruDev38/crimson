package com.enigma.crimson.transactions.application.usecase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountDetailsResult(
        UUID accountId,
        UUID customerId,
        String customerFirstName,
        String customerLastName,
        String customerEmail,
        String currency,
        BigDecimal balance,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
