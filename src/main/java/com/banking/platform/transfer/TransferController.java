package com.banking.platform.transfer;

import com.banking.platform.transfer.dto.TransferRequest;
import com.banking.platform.transfer.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request) {

        TransferResponse response = transferService.transfer(tenantId, idempotencyKey, request);
        return ResponseEntity
                .created(URI.create("/api/v1/transfers/" + response.id()))
                .body(response);
    }
}
