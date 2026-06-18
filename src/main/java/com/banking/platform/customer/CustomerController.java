package com.banking.platform.customer;


import com.banking.platform.common.TenantContext;
import com.banking.platform.customer.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController (CustomerService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<CustomerResponse> create (
            @Valid @RequestBody CreateCustomerRequest request
            ) {
        UUID tenantId = TenantContext.getTenantId();
        CustomerResponse created = service.create(tenantId , request);
         URI location = URI.create("/api/v1/customers/" + created.id());

         return ResponseEntity.created(location).body(created);
    }

    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get (
            @PathVariable UUID id
    ) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.get(tenantId , id));
    }

    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<CustomerSummaryResponse>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String customerNumber,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.list(tenantId, name, customerNumber, pageable));
    }


    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponse> update (
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
            ) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.update(tenantId , id , request));
    }

    @PreAuthorize("hasAnyRole('TELLER','MANAGER','ADMIN')")
    @PostMapping("/{id}/status")
    public ResponseEntity<CustomerResponse> changeStatus (
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequest request
    ){
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(service.changeStatus(tenantId , id , request));
    }

}
