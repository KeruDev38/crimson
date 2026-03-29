package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.port.in.GetAccountDetailsUseCase;
import com.enigma.crimson.transactions.application.port.in.ListAccountTransactionsUseCase;
import com.enigma.crimson.transactions.application.usecase.AccountDetailsResult;
import com.enigma.crimson.transactions.application.usecase.AccountTransactionResult;
import com.enigma.crimson.transactions.domain.account.Account;
import com.enigma.crimson.transactions.domain.customer.Customer;
import com.enigma.crimson.transactions.domain.exception.AccountNotFoundException;
import com.enigma.crimson.transactions.infrastructure.persistence.AccountRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.CustomerRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.TransactionRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AccountQueryService implements GetAccountDetailsUseCase, ListAccountTransactionsUseCase {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public AccountQueryService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            TransactionRecordRepository transactionRecordRepository
    ) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Override
    public AccountDetailsResult getById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        Customer customer = customerRepository.findById(account.getCustomerId())
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        return new AccountDetailsResult(
                account.getId(),
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus().name(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    @Override
    public Page<AccountTransactionResult> list(UUID accountId, Pageable pageable) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        return transactionRecordRepository.findByAccountId(accountId, pageable)
                .map(transaction -> new AccountTransactionResult(
                        transaction.getId(),
                        transaction.getSenderAccountId(),
                        transaction.getReceiverAccountId(),
                        transaction.getAmount(),
                        transaction.getCurrency(),
                        transaction.getStatus().name(),
                        transaction.getReference(),
                        transaction.getCreatedAt()
                ));
    }
}
