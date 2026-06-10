package com.banking.platform.account;

import com.banking.platform.account.dto.AccountOpenRequest;
import com.banking.platform.account.dto.AccountStatusRequest;
import com.banking.platform.customer.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;


    public AccountService (AccountRepository accountRepository , CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AccountResponse open (UUID tenantId , AccountOpenRequest request) {
        if (!customerRepository.existsByIdAndTenantId(request.customerId() , tenantId)) {
            throw new AccountNotFoundException("Customer not found in this tenant");
        }

        String currency = request.currency() == null ? "INR" : request.currency();

        String accountNumber = String.format("ACC-%08d", accountRepository.nextAccountNumber());

        Account account = Account.open(tenantId , request.customerId(), accountNumber , request.type() , currency);

        Account saved = accountRepository.save(account);


        return toResponse(saved);

    }

    @Transactional(readOnly = true)
    public AccountResponse get (UUID tenantId , UUID id) {
        Account acc = accountRepository.findByIdAndTenantId(id , tenantId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));

        return toResponse(acc);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listByCustomer (UUID tenantId , UUID customerId) {
        return accountRepository.findByTenantIdAndCustomerId(tenantId , customerId)
                .stream()
                .map(this::toResponse)
                .toList();

    }

    @Transactional
    public AccountResponse changeStatus(UUID tenantId, UUID id, AccountStatusRequest request) {
        Account account = accountRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));

        switch (request.action()) {
            case FREEZE   -> account.freeze();
            case UNFREEZE -> account.unfreeze();
            case CLOSE    -> account.close();
        }

        return toResponse(account);
    }









    private AccountResponse toResponse(Account a) {
        return new AccountResponse(
                a.getId(),
                a.getAccountNumber(),
                a.getCustomerId(),
                a.getType(),
                a.getStatus(),
                a.getBalance(),
                a.getCurrency(),
                a.getCreatedAt()
        );
    }
}
