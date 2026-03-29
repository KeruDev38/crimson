package com.enigma.crimson.api.shared;

import com.enigma.crimson.transactions.domain.exception.AccountNotFoundException;
import com.enigma.crimson.transactions.domain.exception.AccountStateException;
import com.enigma.crimson.transactions.domain.exception.CurrencyMismatchException;
import com.enigma.crimson.transactions.domain.exception.CustomerNotFoundException;
import com.enigma.crimson.transactions.domain.exception.DuplicateCustomerException;
import com.enigma.crimson.transactions.domain.exception.InsufficientFundsException;
import com.enigma.crimson.transactions.domain.exception.InvalidAccountCreationException;
import com.enigma.crimson.transactions.domain.exception.InvalidTransferException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            AccountNotFoundException.class,
            CustomerNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiErrorResponse handleResourceNotFound(RuntimeException exception) {
        return error("RESOURCE_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler({
            AccountStateException.class,
            CurrencyMismatchException.class,
            DuplicateCustomerException.class,
            InsufficientFundsException.class,
            InvalidAccountCreationException.class,
            InvalidTransferException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiErrorResponse handleBusinessConflict(RuntimeException exception) {
        return error("TRANSFER_CONFLICT", exception.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiErrorResponse handleValidation(Exception exception) {
        return error("VALIDATION_ERROR", exception.getMessage());
    }

    private ApiErrorResponse error(String code, String message) {
        return new ApiErrorResponse(code, message, OffsetDateTime.now(ZoneOffset.UTC));
    }
}
