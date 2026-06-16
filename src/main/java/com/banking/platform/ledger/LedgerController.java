package com.banking.platform.ledger;


import com.banking.platform.common.TenantContext;
import com.banking.platform.ledger.dto.ReconciliationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/v1/ledger")
@RestController
public class LedgerController {

    private final LedgerService service;

    public LedgerController (LedgerService service) {
        this.service = service;
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Map<String, BigDecimal>> balance(
            @PathVariable UUID accountId) {
        UUID tenantId = TenantContext.getTenantId();
        BigDecimal derived = service.deriveBalance(tenantId, accountId);
        return ResponseEntity.ok(Map.of("derivedBalance", derived));
    }

    @GetMapping("/{accountId}/reconcile")
    public ResponseEntity<ReconciliationResponse> reconcile(
            @PathVariable UUID accountId) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.reconcile(tenantId, accountId));
    }
}
