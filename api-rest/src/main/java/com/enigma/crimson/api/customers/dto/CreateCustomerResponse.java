package com.enigma.crimson.api.customers.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateCustomerResponse(
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        OffsetDateTime createdAt
) {
}
