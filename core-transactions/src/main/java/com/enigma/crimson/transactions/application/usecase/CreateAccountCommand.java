package com.enigma.crimson.transactions.application.usecase;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountCommand(
        @NotNull UUID customerId,
        @NotBlank String currency,
        @NotNull @DecimalMin(value = "0.00") BigDecimal initialBalance
) {
}
