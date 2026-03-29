package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.usecase.AccountDetailsResult;
import com.enigma.crimson.transactions.application.usecase.AccountTransactionResult;
import com.enigma.crimson.transactions.domain.account.Account;
import com.enigma.crimson.transactions.domain.account.AccountStatus;
import com.enigma.crimson.transactions.domain.customer.Customer;
import com.enigma.crimson.transactions.domain.exception.AccountNotFoundException;
import com.enigma.crimson.transactions.domain.transaction.TransactionRecord;
import com.enigma.crimson.transactions.domain.transaction.TransactionStatus;
import com.enigma.crimson.transactions.infrastructure.persistence.AccountRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.CustomerRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.TransactionRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @InjectMocks
    private AccountQueryService accountQueryService;

    @Test
    void returnsAccountDetails() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Account account = new Account(accountId, customerId, "MXN", new BigDecimal("80.00"), AccountStatus.ACTIVE);
        Customer customer = new Customer(customerId, "Luna", "Stone", "luna@bank.test");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        AccountDetailsResult result = accountQueryService.getById(accountId);

        assertThat(result.accountId()).isEqualTo(accountId);
        assertThat(result.customerEmail()).isEqualTo("luna@bank.test");
        assertThat(result.balance()).isEqualByComparingTo("80.00");
    }

    @Test
    void returnsPagedTransactionHistory() {
        UUID accountId = UUID.randomUUID();
        TransactionRecord record = new TransactionRecord(
                UUID.randomUUID(),
                accountId,
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                "MXN",
                TransactionStatus.COMPLETED,
                "coffee"
        );

        when(accountRepository.existsById(accountId)).thenReturn(true);
        when(transactionRecordRepository.findByAccountId(accountId, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(record)));

        List<AccountTransactionResult> result = accountQueryService.list(accountId, PageRequest.of(0, 20)).getContent();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).reference()).isEqualTo("coffee");
    }

    @Test
    void failsWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.existsById(accountId)).thenReturn(false);

        assertThatThrownBy(() -> accountQueryService.list(accountId, PageRequest.of(0, 20)))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
