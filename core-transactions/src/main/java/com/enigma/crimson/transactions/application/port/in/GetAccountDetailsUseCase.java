package com.enigma.crimson.transactions.application.port.in;

import com.enigma.crimson.transactions.application.usecase.AccountDetailsResult;

import java.util.UUID;

public interface GetAccountDetailsUseCase {

    AccountDetailsResult getById(UUID accountId);
}
