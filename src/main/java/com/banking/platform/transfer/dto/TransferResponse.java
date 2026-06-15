package com.banking.platform.transfer.dto;

import com.banking.platform.transfer.TransferStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferResponse(UUID id , UUID fromAccountId , UUID toAccountId , BigDecimal amount , String currency , TransferStatus status , String description , OffsetDateTime createdAt) {
}
