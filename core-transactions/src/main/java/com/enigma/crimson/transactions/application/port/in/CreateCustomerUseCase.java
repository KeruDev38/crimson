package com.enigma.crimson.transactions.application.port.in;

import com.enigma.crimson.transactions.application.usecase.CreateCustomerCommand;
import com.enigma.crimson.transactions.application.usecase.CreateCustomerResult;
import jakarta.validation.Valid;

public interface CreateCustomerUseCase {

    CreateCustomerResult create(@Valid CreateCustomerCommand command);
}
