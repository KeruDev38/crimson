package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.port.in.CreateAccountUseCase;
import com.enigma.crimson.transactions.application.usecase.CreateAccountCommand;
import com.enigma.crimson.transactions.application.usecase.CreateAccountResult;
import com.enigma.crimson.transactions.domain.account.Account;
import com.enigma.crimson.transactions.domain.account.AccountStatus;
import com.enigma.crimson.transactions.domain.exception.CustomerNotFoundException;
import com.enigma.crimson.transactions.domain.exception.InvalidAccountCreationException;
import com.enigma.crimson.transactions.infrastructure.persistence.AccountRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;
import java.util.UUID;

@Service
@Validated
public class CreateAccountService implements CreateAccountUseCase {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CreateAccountService(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public CreateAccountResult create(CreateAccountCommand command) {
        String currency = normalizeCurrency(command.currency());
        if (command.initialBalance().signum() < 0) {
            throw new InvalidAccountCreationException("Initial balance cannot be negative");
        }
        customerRepository.findById(command.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));

        Account account = accountRepository.save(new Account(
                UUID.randomUUID(),
                command.customerId(),
                currency,
                command.initialBalance(),
                AccountStatus.ACTIVE
        ));

        return new CreateAccountResult(
                account.getId(),
                account.getCustomerId(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus().name(),
                account.getCreatedAt()
        );
    }

    private String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase(Locale.ROOT);
    }
}
