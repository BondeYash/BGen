package com.banking.platform.ledger.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ReconciliationResponse(
        UUID accountId,
        BigDecimal storedBalance,
        BigDecimal derivedBalance,
        boolean inSync
) {}
