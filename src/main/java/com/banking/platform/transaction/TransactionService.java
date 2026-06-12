package com.banking.platform.transaction;

import com.banking.platform.account.Account;
import com.banking.platform.account.AccountNotFoundException;
import com.banking.platform.account.AccountRepository;
import com.banking.platform.ledger.EntryDirection;
import com.banking.platform.ledger.LedgerAccounts;
import com.banking.platform.ledger.LedgerPosting;
import com.banking.platform.ledger.LedgerRepository;
import com.banking.platform.transaction.dto.RecordTransactionRequest;
import com.banking.platform.transaction.dto.TransactionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              LedgerRepository ledgerRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public TransactionResponse record(UUID tenantId, UUID accountId,
                                      String idempotencyKey, RecordTransactionRequest request) {

        // 1) RECEIPT-NUMBER CHECK — seen this key already? Return the original, move no money.
        var existing = transactionRepository.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        // 2) LOCK THE ACCOUNT — tenant-scoped + SELECT ... FOR UPDATE (others wait here).
        Account account = accountRepository.lockByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        // 3) CURRENCY CHECK — only if the client bothered to send one.
        if (request.currency() != null && !request.currency().equalsIgnoreCase(account.getCurrency())) {
            throw new CurrencyMismatchException(
                    "Transaction currency " + request.currency() + " != account currency " + account.getCurrency());
        }

        // 4) MOVE THE MONEY — the account guards ACTIVE / amount>0 / enough funds.
        switch (request.type()) {
            case DEPOSIT    -> account.credit(request.amount());
            case WITHDRAWAL -> account.debit(request.amount());
        }

        // 5) WRITE THE PASSBOOK LINE — immutable, with the balance snapshot.
        Transaction txn = Transaction.record(
                tenantId, accountId, request.type(), request.amount(),
                account.getCurrency(), account.getBalance(), idempotencyKey, request.description());

        // 6) SAVE NOW (flush) so the UNIQUE receipt-number rule fires inside this method.
        Transaction saved;
        try {
            saved = transactionRepository.saveAndFlush(txn);
        } catch (DataIntegrityViolationException race) {
            // Two identical requests at the SAME instant: the other won the unique key.
            // Nothing we did is kept (this transaction rolls back) — tell the client to retry.
            throw new DuplicateTransactionException(
                    "Idempotency-Key already used; retry to get the original result.");
        }

        // 7) POST THE DOUBLE-ENTRY LEDGER — balanced legs, SAME transaction, SAME @Transactional.
        postLedger(tenantId, saved, account);

        return toResponse(saved);
    }

    /**
     * Write the two balanced ledger legs for this money move.
     * Liability rule: the customer account is a liability of the bank.
     *   DEPOSIT    -> CREDIT customer (owe more), DEBIT  BANK_CASH (asset up)
     *   WITHDRAWAL -> DEBIT  customer (owe less), CREDIT BANK_CASH (asset down)
     * Sums to zero by construction; LedgerPosting refuses anything that doesn't.
     */
    private void postLedger(UUID tenantId, Transaction txn, Account account) {
        String customerRef = account.getId().toString();
        String currency    = account.getCurrency();
        BigDecimal amount  = txn.getAmount();

        EntryDirection customerDir;
        EntryDirection bankDir;
        switch (txn.getType()) {
            case DEPOSIT    -> { customerDir = EntryDirection.CREDIT; bankDir = EntryDirection.DEBIT;  }
            case WITHDRAWAL -> { customerDir = EntryDirection.DEBIT;  bankDir = EntryDirection.CREDIT; }
            default -> throw new IllegalStateException("Unhandled type: " + txn.getType());
        }

        LedgerPosting posting = LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg(LedgerAccounts.BANK_CASH, bankDir,     amount, currency),
                new LedgerPosting.Leg(customerRef,              customerDir, amount, currency)
        ));

        ledgerRepository.saveAll(posting.toEntries(tenantId, txn.getId()));
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getAccountId(), t.getType(), t.getAmount(), t.getCurrency(),
                t.getBalanceAfter(), t.getIdempotencyKey(), t.getDescription(), t.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> list (UUID tenantId , UUID accountId , Pageable pageable) {
        return transactionRepository.findByTenantIdAndAccountId(tenantId , accountId , pageable)
                .map(this::toResponse);
    }
}
