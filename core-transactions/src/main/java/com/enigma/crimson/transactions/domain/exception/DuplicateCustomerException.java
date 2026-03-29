package com.enigma.crimson.transactions.domain.exception;

public class DuplicateCustomerException extends RuntimeException {

    public DuplicateCustomerException(String email) {
        super("A customer with email %s already exists".formatted(email));
    }
}
