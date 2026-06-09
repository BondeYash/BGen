package com.banking.platform.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer , UUID> {

    Page<Customer> findByTenantId (UUID tenantId , Pageable pageable);

    Page<Customer> findByTenantIdAndFullNameContainingIgnoreCase(
            UUID tenantId, String fullName, Pageable pageable);

    Optional<Customer> findByTenantIdAndCustomerNumber(UUID tenantId, String customerNumber);



    Optional<Customer> findByIdAndTenantId (UUID id , UUID tenantId);

    boolean existsByIdAndTenantId (UUID id , UUID tenantId);

    boolean existsByTenantIdAndEmail(UUID tenantId , String email);

    @Query(value = "SELECT nextval('customer_number_seq')" , nativeQuery = true)

    long nextCustomerNumber();
}
