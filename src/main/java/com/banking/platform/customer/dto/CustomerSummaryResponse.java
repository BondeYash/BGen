package com.banking.platform.customer.dto;

import com.banking.platform.customer.CustomerStatus;
import com.banking.platform.customer.CustomerType;
import com.banking.platform.customer.KycStatus;

import java.util.UUID;

public record CustomerSummaryResponse(UUID id, String customerName, CustomerType type , CustomerStatus status, String fullName ,    KycStatus kycstatus) {
}
