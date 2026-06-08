package com.banking.platform.customer;

public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException (String message) {
        super(message);
    }
}
