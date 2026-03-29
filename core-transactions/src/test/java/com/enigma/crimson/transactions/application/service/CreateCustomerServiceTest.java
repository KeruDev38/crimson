package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.usecase.CreateCustomerCommand;
import com.enigma.crimson.transactions.application.usecase.CreateCustomerResult;
import com.enigma.crimson.transactions.domain.customer.Customer;
import com.enigma.crimson.transactions.domain.exception.DuplicateCustomerException;
import com.enigma.crimson.transactions.infrastructure.persistence.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreateCustomerService createCustomerService;

    @Test
    void createsCustomerWhenEmailIsAvailable() {
        when(customerRepository.findByEmailIgnoreCase("luna@bank.test")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateCustomerResult result = createCustomerService.create(new CreateCustomerCommand(
                "Luna",
                "Stone",
                "luna@bank.test"
        ));

        assertThat(result.customerId()).isNotNull();
        assertThat(result.email()).isEqualTo("luna@bank.test");
    }

    @Test
    void rejectsDuplicateCustomerEmail() {
        when(customerRepository.findByEmailIgnoreCase("luna@bank.test")).thenReturn(Optional.of(
                new Customer(UUID.randomUUID(), "Luna", "Stone", "luna@bank.test")
        ));

        assertThatThrownBy(() -> createCustomerService.create(new CreateCustomerCommand(
                "Luna",
                "Stone",
                "luna@bank.test"
        ))).isInstanceOf(DuplicateCustomerException.class);
    }
}
