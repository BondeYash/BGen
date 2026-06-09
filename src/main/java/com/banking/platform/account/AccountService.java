package com.banking.platform.account;

import com.banking.platform.account.dto.AccountOpenRequest;
import com.banking.platform.customer.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new AccountNotFoundException("Customer Not Found in these tenant");
        }

        String currency = request.currency() == null ? "INR" : request.currency();

        String accountNumber = String.format("ACC-%08d", accountRepository.nextAccountNumber());

        Account account = Account.open(tenantId , request.customerId(), accountNumber , request.type() , currency);

        Account saved = accountRepository.save(account);


        return toResponse(saved);

    }

    @Transactional
    public AccountResponse get (UUID id , UUID tenantId) {
        Account acc = accountRepository.findByIdAndTenantId(id , tenantId)
                .orElseThrow(() ->  new AccountNotFoundException("Account Failed to Fetch"));

        return toResponse(acc);
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
