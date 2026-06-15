package com.banking.platform.transfer;


import com.banking.platform.account.Account;
import com.banking.platform.account.AccountNotFoundException;
import com.banking.platform.account.AccountRepository;
import com.banking.platform.ledger.EntryDirection;
import com.banking.platform.ledger.LedgerPosting;
import com.banking.platform.ledger.LedgerRepository;
import com.banking.platform.transaction.CurrencyMismatchException;
import com.banking.platform.transfer.dto.TransferRequest;
import com.banking.platform.transfer.dto.TransferResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;

    public TransferService (TransferRepository transferRepository , AccountRepository accountRepository , LedgerRepository ledgerRepository){
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public TransferResponse transfer (UUID tenantId , String idempotencyKey , TransferRequest request) {
        UUID fromId = request.fromAccountId();
        UUID toId = request.toAccountId();

        if (fromId.equals(toId)) {
            throw new SameAccountTransferException("Cannot transfer to same account " + fromId);
        }

        var existing = transferRepository.findByTenantIdAndIdempotencyKey(tenantId , idempotencyKey);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        UUID firstId = fromId.compareTo(toId) < 0 ? fromId : toId;
        UUID secondId = fromId.compareTo(toId) < 0 ? toId :  fromId;


        Account first = accountRepository.lockByIdAndTenantId(firstId , tenantId)
                .orElseThrow(() -> new AccountNotFoundException("Account not Found" + firstId));
        Account second = accountRepository.lockByIdAndTenantId(secondId , tenantId)
                .orElseThrow(() ->  new AccountNotFoundException("Account Not Found" + secondId));

        Account source =  firstId.equals(fromId) ? first : second;
        Account dest = firstId.equals(fromId) ? second : first;

        if (!source.getCurrency().equals(dest.getCurrency())) {
            throw new CurrencyMismatchException("Source currency " + source.getCurrency() + "and Destination currency "
             + dest.getCurrency());
        }

        if (request.currency() != null && !request.currency().equalsIgnoreCase(source.getCurrency())) {
            throw new CurrencyMismatchException("Request currency " + request.currency() + "!= account" + source.getCurrency());
        }

        source.debit(request.amount());
        dest.credit(request.amount());

        Transfer transfer = Transfer.create(
                tenantId  , fromId , toId , request.amount(),
                source.getCurrency(),   idempotencyKey , request.description()
        );

        Transfer saved;
        try {
            saved =  transferRepository.saveAndFlush(transfer);
        }catch(DataIntegrityViolationException race)  {
            throw new DataIntegrityViolationException("Idempotency Ket already used; retry with new key");
        }

        LedgerPosting posting = LedgerPosting.balanced(List.of(
                new LedgerPosting.Leg(source.getId().toString(), EntryDirection.DEBIT,  request.amount(), source.getCurrency()),
                new LedgerPosting.Leg(dest.getId().toString(),   EntryDirection.CREDIT, request.amount(), source.getCurrency())
        ));
        ledgerRepository.saveAll(posting.toEntries(tenantId, saved.getId()));

        return toResponse(saved);

    }

    private TransferResponse toResponse(Transfer t) {
        return new TransferResponse(
                t.getId(), t.getFromAccountId(), t.getToAccountId(), t.getAmount(),
                t.getCurrency(), t.getStatus(), t.getDescription(), t.getCreatedAt());
    }
}
