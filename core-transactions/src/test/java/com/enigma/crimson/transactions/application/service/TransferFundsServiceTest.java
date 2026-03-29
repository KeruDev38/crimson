package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.usecase.TransferFundsCommand;
import com.enigma.crimson.transactions.application.usecase.TransferFundsResult;
import com.enigma.crimson.transactions.domain.account.Account;
import com.enigma.crimson.transactions.domain.account.AccountStatus;
import com.enigma.crimson.transactions.domain.exception.InsufficientFundsException;
import com.enigma.crimson.transactions.domain.transaction.TransactionRecord;
import com.enigma.crimson.transactions.infrastructure.persistence.AccountRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.TransactionRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferFundsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @InjectMocks
    private TransferFundsService transferFundsService;

    @Test
    void transfersFundsAndStoresCompletedTransaction() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        Account sender = new Account(senderId, UUID.randomUUID(), "MXN", new BigDecimal("200.00"), AccountStatus.ACTIVE);
        Account receiver = new Account(receiverId, UUID.randomUUID(), "MXN", new BigDecimal("50.00"), AccountStatus.ACTIVE);
        TransferFundsCommand command = new TransferFundsCommand(
                senderId,
                receiverId,
                new BigDecimal("25.00"),
                "mxn",
                "rent"
        );

        when(accountRepository.findByIdForUpdate(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(receiverId)).thenReturn(Optional.of(receiver));
        when(transactionRecordRepository.save(any(TransactionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferFundsResult result = transferFundsService.transfer(command);

        assertThat(sender.getBalance()).isEqualByComparingTo("175.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("75.00");
        assertThat(result.status()).isEqualTo("COMPLETED");
        assertThat(result.transactionId()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
    }

    @Test
    void rejectsTransferWhenBalanceIsInsufficient() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        Account sender = new Account(senderId, UUID.randomUUID(), "MXN", new BigDecimal("10.00"), AccountStatus.ACTIVE);
        Account receiver = new Account(receiverId, UUID.randomUUID(), "MXN", new BigDecimal("50.00"), AccountStatus.ACTIVE);

        when(accountRepository.findByIdForUpdate(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(receiverId)).thenReturn(Optional.of(receiver));

        assertThatThrownBy(() -> transferFundsService.transfer(new TransferFundsCommand(
                senderId,
                receiverId,
                new BigDecimal("25.00"),
                "MXN",
                "rent"
        ))).isInstanceOf(InsufficientFundsException.class);
    }
}
