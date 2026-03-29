package com.enigma.crimson.api.accounts.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountTransactionResponse(
        UUID transactionId,
        UUID senderAccountId,
        UUID receiverAccountId,
        BigDecimal amount,
        String currency,
        String status,
        String reference,
        OffsetDateTime createdAt
) {
}
