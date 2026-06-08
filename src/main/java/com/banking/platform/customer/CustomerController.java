package com.banking.platform.customer;


import com.banking.platform.customer.dto.*;
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
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController (CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create (
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreateCustomerRequest request
            ) {
        CustomerResponse created = service.create(tenantId , request);
         URI location = URI.create("/api/v1/customers/" + created.id());

         return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get (
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.get(tenantId , id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerSummaryResponse>> list(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String customerNumber,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(service.list(tenantId, name, customerNumber, pageable));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponse> update (
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
            ) {
        return ResponseEntity.ok(service.update(tenantId , id , request));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<CustomerResponse> changeStatus (
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequest request
    ){
        return ResponseEntity.ok(service.changeStatus(tenantId , id , request));
    }

}
