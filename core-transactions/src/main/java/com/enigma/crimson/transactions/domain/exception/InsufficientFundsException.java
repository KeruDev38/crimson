package com.enigma.crimson.transactions.domain.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(UUID accountId, BigDecimal balance, BigDecimal requestedAmount) {
        super("Account %s has balance %s and cannot transfer %s".formatted(accountId, balance, requestedAmount));
    }
}
