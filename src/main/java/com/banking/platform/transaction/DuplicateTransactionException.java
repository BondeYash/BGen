package com.banking.platform.transaction;


public class DuplicateTransactionException extends RuntimeException{
    public DuplicateTransactionException (String message) {
        super(message);
    }
}
