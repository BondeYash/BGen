package com.banking.platform.account;

import com.banking.platform.account.dto.AccountOpenRequest;
import com.banking.platform.account.dto.AccountStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

   @PostMapping
    public ResponseEntity<AccountResponse> openAccount (
           @RequestHeader("X-Tenant-Id")UUID tenantId,
           @Valid @RequestBody AccountOpenRequest request
           ) {
       AccountResponse created =  service.open(tenantId , request);
       URI location = URI.create("/api/v1/accounts/" + created.id());

       return ResponseEntity.created(location).body(created);

   }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.get(tenantId, id));
    }

    // List one customer's accounts: /api/v1/accounts?customerId={uuid}
    @GetMapping
    public ResponseEntity<List<AccountResponse>> listByCustomer(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam UUID customerId
    ) {
        return ResponseEntity.ok(service.listByCustomer(tenantId, customerId));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<AccountResponse> changeStatus (
            @RequestHeader("X-Tenant-Id")UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody AccountStatusRequest request
            ) {
        return ResponseEntity.ok(service.changeStatus(tenantId , id , request));
    }



}
