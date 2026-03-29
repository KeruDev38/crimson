package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.usecase.CreateAccountCommand;
import com.enigma.crimson.transactions.application.usecase.CreateAccountResult;
import com.enigma.crimson.transactions.domain.customer.Customer;
import com.enigma.crimson.transactions.domain.exception.CustomerNotFoundException;
import com.enigma.crimson.transactions.domain.exception.InvalidAccountCreationException;
import com.enigma.crimson.transactions.infrastructure.persistence.AccountRepository;
import com.enigma.crimson.transactions.infrastructure.persistence.CustomerRepository;
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
class CreateAccountServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreateAccountService createAccountService;

    @Test
    void createsAccountForExistingCustomer() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(
                new Customer(customerId, "Luna", "Stone", "luna@bank.test")
        ));
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountResult result = createAccountService.create(new CreateAccountCommand(
                customerId,
                "mxn",
                new BigDecimal("125.50")
        ));

        assertThat(result.accountId()).isNotNull();
        assertThat(result.customerId()).isNotNull();
        assertThat(result.currency()).isEqualTo("MXN");
        assertThat(result.balance()).isEqualByComparingTo("125.50");
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void rejectsNegativeInitialBalance() {
        UUID customerId = UUID.randomUUID();

        assertThatThrownBy(() -> createAccountService.create(new CreateAccountCommand(
                customerId,
                "MXN",
                new BigDecimal("-1.00")
        ))).isInstanceOf(InvalidAccountCreationException.class);
    }

    @Test
    void rejectsUnknownCustomer() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createAccountService.create(new CreateAccountCommand(
                customerId,
                "MXN",
                BigDecimal.ZERO
        ))).isInstanceOf(CustomerNotFoundException.class);
    }
}
