package com.banking.platform.transaction;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;


public interface TransactionRepository extends JpaRepository <Transaction , UUID> {


    Optional<Transaction> findByTenantIdAndIdempotencyKey (UUID tenantId , String idempotencyKey);

    Page<Transaction> findByTenantIdAndAccountId (UUID tenantId , UUID accountId , Pageable pageable);

}
