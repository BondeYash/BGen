package com.banking.platform.transaction.dto;

import com.banking.platform.transaction.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RecordTransactionRequest(
        @NotNull(message = "type is required")
        TransactionType type,                       // DEPOSIT or WITHDRAWAL

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        @Digits(integer = 15, fraction = 4, message = "amount: max 15 digits, 4 decimals")
        BigDecimal amount,

        @Size(min = 3, max = 3, message = "currency must be a 3-letter code")
        String currency,                            // optional; if null we use the account's currency

        @Size(max = 255)
        String description                          // optional note
) {}
