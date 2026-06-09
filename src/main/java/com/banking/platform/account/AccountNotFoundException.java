package com.banking.platform.account;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException (String message) {
        super(message);
    }
}
