package com.banking.platform.account;

public class InvalidAccountStateException extends RuntimeException{
    public InvalidAccountStateException(String message) {
        super(message);
    }
}
