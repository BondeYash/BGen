package com.banking.platform.account;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    // The owning customer — referenced BY ID (a value), not a @ManyToOne object.
    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;

    @Column(name = "account_number", nullable = false, length = 20, updatable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20, updatable = false)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    // Money: BigDecimal mapped to NUMERIC(19,4)
    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Account() {
        // JPA only
    }

    private Account(UUID tenantId, UUID customerId, String accountNumber,
                    AccountType type, String currency) {
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.currency = currency;
        this.status = AccountStatus.ACTIVE;        // forced initial state
        this.balance = BigDecimal.ZERO;            // opens empty
    }

    // Factory: the ONLY way to open an account
    public static Account open(UUID tenantId, UUID customerId, String accountNumber,
                               AccountType type, String currency) {
        return new Account(tenantId, customerId, accountNumber, type, currency);
    }

    // ---- lifecycle transitions (state machine) ----

    public void freeze() {
        if (status != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException("Only ACTIVE accounts can be frozen (was " + status + ")");
        }
        this.status = AccountStatus.FROZEN;
    }

    public void unfreeze() {
        if (status != AccountStatus.FROZEN) {
            throw new InvalidAccountStateException("Only FROZEN accounts can be unfrozen (was " + status + ")");
        }
        this.status = AccountStatus.ACTIVE;
    }

    public void close() {
        if (status == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException("Account already CLOSED");
        }
        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidAccountStateException("Cannot close an account with a non-zero balance");
        }
        this.status = AccountStatus.CLOSED;
    }

    // ---- getters only. NO setters. ----
    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getType() { return type; }
    public AccountStatus getStatus() { return status; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public long getVersion() { return version; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
