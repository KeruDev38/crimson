package com.enigma.crimson.api.shared;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        String code,
        String message,
        OffsetDateTime timestamp
) {
}
