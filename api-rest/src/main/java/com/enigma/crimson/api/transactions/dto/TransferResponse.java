package com.enigma.crimson.api.transactions.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        String status,
        OffsetDateTime createdAt
) {
}
