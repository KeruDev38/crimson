package com.enigma.crimson.api.transactions;

import com.enigma.crimson.api.transactions.dto.TransferRequest;
import com.enigma.crimson.api.transactions.dto.TransferResponse;
import com.enigma.crimson.transactions.application.port.in.TransferFundsUseCase;
import com.enigma.crimson.transactions.application.usecase.TransferFundsCommand;
import com.enigma.crimson.transactions.application.usecase.TransferFundsResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransactionsController {

    private final TransferFundsUseCase transferFundsUseCase;

    public TransactionsController(TransferFundsUseCase transferFundsUseCase) {
        this.transferFundsUseCase = transferFundsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        TransferFundsResult result = transferFundsUseCase.transfer(new TransferFundsCommand(
                request.senderAccountId(),
                request.receiverAccountId(),
                request.amount(),
                request.currency(),
                request.reference()
        ));

        return new TransferResponse(
                result.transactionId(),
                result.status(),
                result.createdAt()
        );
    }
}
