package com.banking.platform.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest (
       @Email(message = "Email Must be valid")
       @Size(max = 255)
       String email,

       @Size(max = 20)
       String phone

) {
}
