package com.enigma.crimson.transactions.application.usecase;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferFundsResult(
        UUID transactionId,
        String status,
        OffsetDateTime createdAt
) {
}
