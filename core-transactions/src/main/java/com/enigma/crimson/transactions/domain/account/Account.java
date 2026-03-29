package com.enigma.crimson.transactions.domain.account;

import com.enigma.crimson.transactions.domain.exception.AccountStateException;
import com.enigma.crimson.transactions.domain.exception.CurrencyMismatchException;
import com.enigma.crimson.transactions.domain.exception.InsufficientFundsException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Account() {
    }

    public Account(UUID id, UUID customerId, String currency, BigDecimal balance, AccountStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.currency = normalizeCurrency(currency);
        this.balance = balance;
        this.status = status;
        this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void debit(BigDecimal amount, String transferCurrency) {
        assertActive();
        assertCurrency(transferCurrency);

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(id, balance, amount);
        }

        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount, String transferCurrency) {
        assertActive();
        assertCurrency(transferCurrency);
        balance = balance.add(amount);
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    private void assertActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new AccountStateException(id, status);
        }
    }

    private void assertCurrency(String transferCurrency) {
        String normalized = normalizeCurrency(transferCurrency);
        if (!currency.equals(normalized)) {
            throw new CurrencyMismatchException(id, currency, normalized);
        }
    }

    private String normalizeCurrency(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
