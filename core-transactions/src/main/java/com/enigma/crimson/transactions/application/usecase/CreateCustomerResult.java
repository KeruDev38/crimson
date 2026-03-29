package com.enigma.crimson.transactions.application.usecase;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateCustomerResult(
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        OffsetDateTime createdAt
) {
}
