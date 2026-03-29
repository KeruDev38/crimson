package com.enigma.crimson.transactions.application.service;

import com.enigma.crimson.transactions.application.port.in.CreateCustomerUseCase;
import com.enigma.crimson.transactions.application.usecase.CreateCustomerCommand;
import com.enigma.crimson.transactions.application.usecase.CreateCustomerResult;
import com.enigma.crimson.transactions.domain.customer.Customer;
import com.enigma.crimson.transactions.domain.exception.DuplicateCustomerException;
import com.enigma.crimson.transactions.infrastructure.persistence.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CreateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CreateCustomerResult create(CreateCustomerCommand command) {
        customerRepository.findByEmailIgnoreCase(command.email()).ifPresent(customer -> {
            throw new DuplicateCustomerException(command.email());
        });

        Customer customer = customerRepository.save(new Customer(
                UUID.randomUUID(),
                command.firstName(),
                command.lastName(),
                command.email()
        ));

        return new CreateCustomerResult(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }
}
