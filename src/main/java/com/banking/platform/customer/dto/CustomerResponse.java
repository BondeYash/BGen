package com.banking.platform.customer.dto;

import com.banking.platform.customer.CustomerStatus;
import com.banking.platform.customer.CustomerType;
import com.banking.platform.customer.KycStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CustomerResponse (
        UUID id,
        String customerNumber,
        CustomerType type,
        CustomerStatus status,
        String fullName,
        String email,
        String phone,
        KycStatus kycStatus,
        OffsetDateTime createdAt
){
}
