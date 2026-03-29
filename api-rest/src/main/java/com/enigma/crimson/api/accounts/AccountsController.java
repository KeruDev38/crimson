package com.enigma.crimson.api.accounts;

import com.enigma.crimson.api.accounts.dto.CreateAccountRequest;
import com.enigma.crimson.api.accounts.dto.CreateAccountResponse;
import com.enigma.crimson.api.accounts.dto.AccountResponse;
import com.enigma.crimson.api.accounts.dto.AccountTransactionResponse;
import com.enigma.crimson.api.shared.PageResponse;
import com.enigma.crimson.transactions.application.port.in.CreateAccountUseCase;
import com.enigma.crimson.transactions.application.port.in.GetAccountDetailsUseCase;
import com.enigma.crimson.transactions.application.port.in.ListAccountTransactionsUseCase;
import com.enigma.crimson.transactions.application.usecase.AccountDetailsResult;
import com.enigma.crimson.transactions.application.usecase.AccountTransactionResult;
import com.enigma.crimson.transactions.application.usecase.CreateAccountCommand;
import com.enigma.crimson.transactions.application.usecase.CreateAccountResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/accounts")
public class AccountsController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountDetailsUseCase getAccountDetailsUseCase;
    private final ListAccountTransactionsUseCase listAccountTransactionsUseCase;

    public AccountsController(
            CreateAccountUseCase createAccountUseCase,
            GetAccountDetailsUseCase getAccountDetailsUseCase,
            ListAccountTransactionsUseCase listAccountTransactionsUseCase
    ) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountDetailsUseCase = getAccountDetailsUseCase;
        this.listAccountTransactionsUseCase = listAccountTransactionsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountResult result = createAccountUseCase.create(new CreateAccountCommand(
                request.customerId(),
                request.currency(),
                request.initialBalance()
        ));

        return new CreateAccountResponse(
                result.accountId(),
                result.customerId(),
                result.currency(),
                result.balance(),
                result.status(),
                result.createdAt()
        );
    }

    @GetMapping("/{accountId}")
    public AccountResponse getById(@PathVariable("accountId") UUID accountId) {
        AccountDetailsResult result = getAccountDetailsUseCase.getById(accountId);
        return new AccountResponse(
                result.accountId(),
                result.customerId(),
                result.customerFirstName(),
                result.customerLastName(),
                result.customerEmail(),
                result.currency(),
                result.balance(),
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    @GetMapping("/{accountId}/transactions")
    public PageResponse<AccountTransactionResponse> listTransactions(
            @PathVariable("accountId") UUID accountId,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Page<AccountTransactionResponse> result = listAccountTransactionsUseCase.list(
                accountId,
                PageRequest.of(page, size, Sort.unsorted())
        ).map(this::toResponse);

        return PageResponse.from(result);
    }

    private AccountTransactionResponse toResponse(AccountTransactionResult result) {
        return new AccountTransactionResponse(
                result.transactionId(),
                result.senderAccountId(),
                result.receiverAccountId(),
                result.amount(),
                result.currency(),
                result.status(),
                result.reference(),
                result.createdAt()
        );
    }
}
