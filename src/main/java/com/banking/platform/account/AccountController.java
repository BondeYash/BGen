package com.banking.platform.account;

import com.banking.platform.common.TenantContext;
import com.banking.platform.account.dto.AccountOpenRequest;
import com.banking.platform.account.dto.AccountStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

   private final AccountService service;

   public AccountController (AccountService service) {
       this.service = service;
   }

   @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
   @PostMapping
    public ResponseEntity<AccountResponse> openAccount (
           @Valid @RequestBody AccountOpenRequest request
           ) {
       UUID tenantId = TenantContext.getTenantId();
       AccountResponse created =  service.open(tenantId , request);
       URI location = URI.create("/api/v1/accounts/" + created.id());

       return ResponseEntity.created(location).body(created);

   }

    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(
            @PathVariable UUID id
    ) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.get(tenantId, id));
    }

    // List one customer's accounts: /api/v1/accounts?customerId={uuid}
    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> listByCustomer(
            @RequestParam UUID customerId
    ) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.listByCustomer(tenantId, customerId));
    }

    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @PostMapping("/{id}/status")
    public ResponseEntity<AccountResponse> changeStatus (
            @PathVariable UUID id,
            @Valid @RequestBody AccountStatusRequest request
            ) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.changeStatus(tenantId , id , request));
    }



}
