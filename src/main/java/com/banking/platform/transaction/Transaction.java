package com.banking.platform.transaction;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20, updatable = false)
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal balanceAfter;

    @Column(name = "idempotency_key", nullable = false, length = 80, updatable = false)
    private String idempotencyKey;

    @Column(name = "description", length = 255, updatable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Transaction() {
        // JPA only
    }

    private Transaction(UUID tenantId, UUID accountId, TransactionType type, BigDecimal amount,
                        String currency, BigDecimal balanceAfter, String idempotencyKey, String description) {
        this.tenantId = tenantId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.balanceAfter = balanceAfter;
        this.idempotencyKey = idempotencyKey;
        this.description = description;
    }
    public static Transaction record(UUID tenantId, UUID accountId, TransactionType type, BigDecimal amount,
                                    String currency, BigDecimal balanceAfter, String idempotencyKey, String description) {
        return new Transaction(tenantId, accountId, type, amount, currency, balanceAfter, idempotencyKey, description);
    }
    
    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
