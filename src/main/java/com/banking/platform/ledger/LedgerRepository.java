package com.banking.platform.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {

    @Query("""
            select coalesce(sum(case when e.direction = :credit
                                     then e.amount
                                     else e.amount * -1 end), 0)
            from LedgerEntry e
            where e.tenantId = :tenantId and e.accountRef = :accountRef
            """)
    BigDecimal deriveBalance(@Param("tenantId") UUID tenantId,
                             @Param("accountRef") String accountRef,
                             @Param("credit") EntryDirection credit);
}
