package com.banking.platform.customer;

import com.banking.platform.customer.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    // Constructor injection: Spring hands us the repository.
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }
    // Turn the entity into the LIGHT list DTO (fewer fields than toResponse).
    private CustomerSummaryResponse toSummary(Customer c) {
        return new CustomerSummaryResponse(
                c.getId(),
                c.getCustomerNumber(),
                c.getType(),
                c.getStatus(),
                c.getFullName(),
                c.getKycStatus()
        );
    }



    @Transactional
    public CustomerResponse create(UUID tenantId, CreateCustomerRequest request) {

        // --- business validation (layer 2: rules, not just shape) ---
        validateTypeSpecificFields(request);

        if (request.email() != null
                && repository.existsByTenantIdAndEmail(tenantId, request.email())) {
            throw new DuplicateEmailException(
                    "A customer with this email already exists for this tenant");
        }

        // --- generate the human-facing number from the DB sequence ---
        String customerNumber = String.format("CUST-%08d", repository.nextCustomerNumber());

        // --- build a valid customer via the factory (starts PENDING / NOT_VERIFIED) ---
        Customer customer = Customer.create(
                tenantId,
                customerNumber,
                request.type(),
                request.fullName(),
                request.email(),
                request.phone(),
                request.dateOfBirth(),
                request.registrationNo()
        );

        // --- save (INSERT) and map to the public response ---
        Customer saved = repository.save(customer);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(UUID tenantId, UUID id) {
        Customer customer = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new InvalidCustomerStateException(
                        "Customer not found: " + id));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerSummaryResponse> list(
            UUID tenantId, String name, String customerNumber, Pageable pageable) {

        if (customerNumber != null && !customerNumber.isBlank()) {
            return repository.findByTenantIdAndCustomerNumber(tenantId, customerNumber)
                    .map(this::toSummary)
                    .map(dto -> (Page<CustomerSummaryResponse>) new PageImpl<>(List.of(dto)))
                    .orElseGet(() -> Page.empty(pageable));   // no match = empty page, not 404
        }

        if (name != null && !name.isBlank()) {
            return repository.findByTenantIdAndFullNameContainingIgnoreCase(tenantId, name, pageable)
                    .map(this::toSummary);
        }

        return repository.findByTenantId(tenantId, pageable)
                .map(this::toSummary);
    }

    @Transactional
    public CustomerResponse update (UUID tenantId , UUID id , UpdateCustomerRequest  request) {

        Customer customer = repository.findByIdAndTenantId(id , tenantId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found" + id));

        if (request.email() != null
                && !request.email().equals(customer.getEmail())
                && repository.existsByTenantIdAndEmail(tenantId, request.email())) {
            throw new DuplicateEmailException("Email already used in this tenant");
        }

        customer.changeContact(request.email(), request.phone());

        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse changeStatus (UUID tenantId , UUID id , ChangeStatusRequest request) {

        Customer customer = repository.findByIdAndTenantId(id , tenantId)
                .orElseThrow( () -> new CustomerNotFoundException("Customer Not Found" + id));

        switch(request.action()) {
            case ACTIVE -> customer.activate();
            case SUSPENDED -> customer.suspend();
            case CLOSED -> customer.close();
        }

        return toResponse(customer);
    }


    // INDIVIDUAL needs a date of birth; CORPORATE needs a registration number.
    private void validateTypeSpecificFields(CreateCustomerRequest request) {
        if (request.type() == CustomerType.INDIVIDUAL && request.dateOfBirth() == null) {
            throw new IllegalArgumentException("dateOfBirth is required for INDIVIDUAL customers");
        }
        if (request.type() == CustomerType.CORPORATE && request.registrationNo() == null) {
            throw new IllegalArgumentException("registrationNo is required for CORPORATE customers");
        }
    }

    // Turn the internal entity into the safe public DTO.
    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getCustomerNumber(),
                c.getType(),
                c.getStatus(),
                c.getFullName(),
                c.getEmail(),
                c.getPhone(),
                c.getKycStatus(),
                c.getCreatedAt()
        );
    }
}
