package com.banking.platform.account;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    // tenant-scoped fetch of one account
    Optional<Account> findByIdAndTenantId(UUID id, UUID tenantId);

    // all accounts of a customer (for a list endpoint later)
    java.util.List<Account> findByTenantIdAndCustomerId(UUID tenantId, UUID customerId);

    // next human-facing number from the DB sequence
    @Query(value = "SELECT nextval('account_number_seq')", nativeQuery = true)
    long nextAccountNumber();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id and a.tenantId = :tenantId")
    Optional<Account> lockByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);


}
