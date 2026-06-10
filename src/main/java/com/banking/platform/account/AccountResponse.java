package com.banking.platform.account;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponse (
        UUID id,
        String accountNumber,
        UUID customerId,
        AccountType type,
        AccountStatus status,
        BigDecimal balance,
        String currency,
        OffsetDateTime createdAt


) {
}
