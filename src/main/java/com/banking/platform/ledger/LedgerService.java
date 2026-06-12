package com.banking.platform.ledger;

import com.banking.platform.account.Account;
import com.banking.platform.account.AccountNotFoundException;
import com.banking.platform.account.AccountRepository;
import com.banking.platform.ledger.dto.ReconciliationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;

    public LedgerService(LedgerRepository ledgerRepository, AccountRepository accountRepository) {
        this.ledgerRepository = ledgerRepository;
        this.accountRepository = accountRepository;
    }

    /** Balance re-computed from the immutable ledger lines (the source of truth). */
    @Transactional(readOnly = true)
    public BigDecimal deriveBalance(UUID tenantId, UUID accountId) {
        return ledgerRepository.deriveBalance(tenantId, accountId.toString(), EntryDirection.CREDIT);
    }

    /** Compare the cached accounts.balance against the ledger-derived figure. */
    @Transactional(readOnly = true)
    public ReconciliationResponse reconcile(UUID tenantId, UUID accountId) {
        Account account = accountRepository.findByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        BigDecimal stored  = account.getBalance();
        BigDecimal derived = deriveBalance(tenantId, accountId);
        boolean inSync = stored.compareTo(derived) == 0;   // compareTo, NOT equals — scale trap

        return new ReconciliationResponse(accountId, stored, derived, inSync);
    }
}
