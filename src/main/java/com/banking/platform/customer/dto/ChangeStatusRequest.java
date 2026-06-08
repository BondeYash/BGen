package com.banking.platform.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest (
        @NotNull(message = "Action is Required")
        StateAction action
) {
    public enum StateAction {ACTIVE , SUSPENDED , CLOSED}
}
