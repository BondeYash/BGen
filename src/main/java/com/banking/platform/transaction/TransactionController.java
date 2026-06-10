package com.banking.platform.transaction;


import com.banking.platform.transaction.dto.RecordTransactionRequest;
import com.banking.platform.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController (TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> record (
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @PathVariable UUID accountId,
            @Valid @RequestBody RecordTransactionRequest request

            ){
        TransactionResponse created = service.record(tenantId, accountId, idempotencyKey, request);
        URI location = URI.create("/api/v1/accounts/" + accountId + "/transactions/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> list(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID accountId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.list(tenantId, accountId, pageable));
    }
}
