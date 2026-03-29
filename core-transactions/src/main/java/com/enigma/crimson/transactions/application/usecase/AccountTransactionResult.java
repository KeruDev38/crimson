package com.enigma.crimson.transactions.application.usecase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountTransactionResult(
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
