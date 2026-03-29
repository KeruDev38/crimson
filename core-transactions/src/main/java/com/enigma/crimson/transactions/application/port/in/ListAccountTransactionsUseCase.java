package com.enigma.crimson.transactions.application.port.in;

import com.enigma.crimson.transactions.application.usecase.AccountTransactionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListAccountTransactionsUseCase {

    Page<AccountTransactionResult> list(UUID accountId, Pageable pageable);
}
