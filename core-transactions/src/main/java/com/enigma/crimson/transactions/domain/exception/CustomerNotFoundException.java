package com.enigma.crimson.transactions.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Customer %s was not found".formatted(customerId));
    }
}
