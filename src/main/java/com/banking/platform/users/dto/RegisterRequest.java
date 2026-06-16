package com.banking.platform.users.dto;

import com.banking.platform.users.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record RegisterRequest (@NotNull UUID tenantId,
                               @NotBlank @Email String email,
                               @NotBlank @Size(min = 8 , max = 72) String password,
                               String fullName,
                               UUID customerId,
                               Set<Role> roles) {
}
