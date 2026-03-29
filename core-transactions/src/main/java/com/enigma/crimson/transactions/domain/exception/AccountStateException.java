package com.enigma.crimson.transactions.domain.exception;

import com.enigma.crimson.transactions.domain.account.AccountStatus;

import java.util.UUID;

public class AccountStateException extends RuntimeException {

    public AccountStateException(UUID accountId, AccountStatus status) {
        super("Account %s is not available for transfers because it is %s".formatted(accountId, status));
    }
}
