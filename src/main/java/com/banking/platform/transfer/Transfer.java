package com.banking.platform.transfer;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id" , updatable = false , nullable = false)
    private UUID id;

    @Column(name = "tenant_id" , nullable = false , updatable = false)
    private UUID tenantId;

    @Column(name = "from_account_id" , nullable = false , updatable = false)
    private UUID fromAccountId;

    @Column(name = "to_account_id" , nullable = false , updatable = false)
    private UUID toAccountId;

    @Column(name = "amount" , nullable = false , precision = 19, scale = 4, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency" , nullable = false , length = 3 , updatable = false)
    private String currency;

    @Column(name = "idempotency_key" , nullable = false , length = 80 , updatable = false)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false , length = 20)
    private TransferStatus status;

    @Column(name = "description" , length = 255 , updatable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at" , nullable = false , updatable = false)
    private OffsetDateTime createdAt;

    protected Transfer () {

    }


    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    private Transfer (UUID tenantId , UUID fromAccountId , UUID toAccountId , BigDecimal amount , String currency ,
                      String idempotencyKey , String description
                                    )
    {
        this.tenantId = tenantId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
        this.idempotencyKey = idempotencyKey;
        this.description = description;
        this.status = TransferStatus.COMPLETED;
    }

    public static Transfer create (UUID tenantId , UUID fromAccountId , UUID toAccountId , BigDecimal amount,
                                   String currency , String idempotencyKey , String description
                                   ){
        return new Transfer(tenantId , fromAccountId , toAccountId , amount , currency , idempotencyKey, description);
    }




}
