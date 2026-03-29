package com.enigma.crimson.transactions.application.port.in;

import com.enigma.crimson.transactions.application.usecase.CreateAccountCommand;
import com.enigma.crimson.transactions.application.usecase.CreateAccountResult;
import jakarta.validation.Valid;

public interface CreateAccountUseCase {

    CreateAccountResult create(@Valid CreateAccountCommand command);
}
