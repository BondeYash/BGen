package com.banking.platform.account.dto;

import jakarta.validation.constraints.NotNull;

public record AccountStatusRequest(
        @NotNull(message = "Action is required")
        Action action
) {
    public enum Action { FREEZE, UNFREEZE, CLOSE }
}
