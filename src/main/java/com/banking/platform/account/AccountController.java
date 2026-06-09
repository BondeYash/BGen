package com.banking.platform.account;

import com.banking.platform.account.dto.AccountOpenRequest;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
       URI location = URI.create("/api/v1/account/" + created.id());

       return ResponseEntity.created(location).body(created);

   }

}
