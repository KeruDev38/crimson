package com.enigma.crimson.transactions.domain.exception;

import java.util.UUID;

public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException(UUID accountId, String accountCurrency, String transferCurrency) {
        super("Account %s uses %s but transfer requested %s".formatted(accountId, accountCurrency, transferCurrency));
    }
}
