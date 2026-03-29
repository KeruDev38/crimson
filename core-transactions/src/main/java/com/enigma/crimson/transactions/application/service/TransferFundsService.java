package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.port.in.TransferFundsUseCase;
import com.enigma.crimson.transactions.application.usecase.TransferFundsCommand;
import com.enigma.crimson.transactions.application.usecase.TransferFundsResult;
import com.enigma.crimson.transactions.domain.account.Account;
import com.enigma.crimson.transactions.domain.exception.AccountNotFoundException;
import com.enigma.crimson.transactions.domain.exception.InvalidTransferException;
import com.enigma.crimson.transactions.domain.transaction.TransactionRecord;
import com.enigma.crimson.transactions.domain.transaction.TransactionStatus;
import com.enigma.crimson.transactions.infrastructure.persistence.AccountRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.TransactionRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@Validated
public class TransferFundsService implements TransferFundsUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransferFundsService(
            AccountRepository accountRepository,
            TransactionRecordRepository transactionRecordRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Override
    @Transactional
    public TransferFundsResult transfer(TransferFundsCommand command) {
        if (command.senderAccountId().equals(command.receiverAccountId())) {
            throw new InvalidTransferException("Sender and receiver accounts must be different");
        }

        String normalizedCurrency = command.currency().trim().toUpperCase(Locale.ROOT);
        Account sender = accountRepository.findByIdForUpdate(command.senderAccountId())
                .orElseThrow(() -> new AccountNotFoundException(command.senderAccountId()));
        Account receiver = accountRepository.findByIdForUpdate(command.receiverAccountId())
                .orElseThrow(() -> new AccountNotFoundException(command.receiverAccountId()));

        sender.debit(command.amount(), normalizedCurrency);
        receiver.credit(command.amount(), normalizedCurrency);

        TransactionRecord transaction = transactionRecordRepository.save(new TransactionRecord(
                UUID.randomUUID(),
                sender.getId(),
                receiver.getId(),
                command.amount(),
                normalizedCurrency,
                TransactionStatus.COMPLETED,
                command.reference()
        ));

        return new TransferFundsResult(
                transaction.getId(),
                transaction.getStatus().name(),
                transaction.getCreatedAt()
        );
    }
}
