package com.banking.platform.transaction.dto;

import com.banking.platform.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID accountId,
        TransactionType type,
        BigDecimal amount,
        String currency,
        BigDecimal balanceAfter,      // the balance right after this move
        String idempotencyKey,
        String description,
        OffsetDateTime createdAt
) {}
