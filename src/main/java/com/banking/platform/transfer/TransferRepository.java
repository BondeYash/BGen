package com.banking.platform.transfer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepository extends JpaRepository <Transfer , UUID> {
    Optional <Transfer> findByTenantIdAndIdempotencyKey (UUID tenantId , String idempotencyKey);
}
