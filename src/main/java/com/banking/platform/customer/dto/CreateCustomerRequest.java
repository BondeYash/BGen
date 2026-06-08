package com.banking.platform.customer.dto;

import com.banking.platform.customer.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCustomerRequest(

        @NotNull(message = "type is required")
        CustomerType type,

        @NotBlank(message = "fullName is required")
        @Size(max = 150, message = "fullName too long")
        String fullName,

        @Email(message = "email must be valid")
        @Size(max = 255)
        String email,

        @Size(max = 20)
        String phone,

        LocalDate dateOfBirth,

        @Size(max = 50)
        String registrationNo
) {}
