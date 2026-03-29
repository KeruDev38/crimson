package com.enigma.crimson.api.customers;

import com.enigma.crimson.api.customers.dto.CreateCustomerRequest;
import com.enigma.crimson.api.customers.dto.CreateCustomerResponse;
import com.enigma.crimson.transactions.application.port.in.CreateCustomerUseCase;
import com.enigma.crimson.transactions.application.usecase.CreateCustomerCommand;
import com.enigma.crimson.transactions.application.usecase.CreateCustomerResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {

    private final CreateCustomerUseCase createCustomerUseCase;

    public CustomersController(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        CreateCustomerResult result = createCustomerUseCase.create(new CreateCustomerCommand(
                request.firstName(),
                request.lastName(),
                request.email()
        ));

        return new CreateCustomerResponse(
                result.customerId(),
                result.firstName(),
                result.lastName(),
                result.email(),
                result.createdAt()
        );
    }
}
