package com.enigma.crimson.transactions.domain.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID accountId) {
        super("Account %s was not found".formatted(accountId));
    }
}
