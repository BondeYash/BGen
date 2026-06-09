package com.banking.platform.account.dto;

import com.banking.platform.account.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AccountOpenRequest (
        @NotNull(message = "Customer Id is Required")
        UUID customerId,

        @NotNull(message = "Account Type is Required")
        AccountType type,

        @Size(min = 3 , max = 3 , message = "Currency or ISO code for Currency is Required")
        String currency
) {
}
