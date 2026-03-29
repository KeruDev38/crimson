package com.enigma.crimson.transactions.application.port.in;

import com.enigma.crimson.transactions.application.usecase.TransferFundsCommand;
import com.enigma.crimson.transactions.application.usecase.TransferFundsResult;
import jakarta.validation.Valid;

public interface TransferFundsUseCase {

    TransferFundsResult transfer(@Valid TransferFundsCommand command);
}
