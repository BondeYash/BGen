package com.banking.platform.ledger;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "transaction_id", nullable = false, updatable = false)
    private UUID transactionId;

    @Column(name = "account_ref", nullable = false, length = 64, updatable = false)
    private String accountRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 6, updatable = false)
    private EntryDirection direction;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected LedgerEntry() {
        // JPA only
    }

    private LedgerEntry(UUID tenantId, UUID transactionId, String accountRef,
                        EntryDirection direction, BigDecimal amount, String currency) {
        this.tenantId = tenantId;
        this.transactionId = transactionId;
        this.accountRef = accountRef;
        this.direction = direction;
        this.amount = amount;
        this.currency = currency;
    }

    public static LedgerEntry of(UUID tenantId, UUID transactionId, String accountRef,
                                 EntryDirection direction, BigDecimal amount, String currency) {
        return new LedgerEntry(tenantId, transactionId, accountRef, direction, amount, currency);
    }

    public UUID getId()             { return id; }
    public UUID getTenantId()       { return tenantId; }
    public UUID getTransactionId()  { return transactionId; }
    public String getAccountRef()   { return accountRef; }
    public EntryDirection getDirection() { return direction; }
    public BigDecimal getAmount()   { return amount; }
    public String getCurrency()     { return currency; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
