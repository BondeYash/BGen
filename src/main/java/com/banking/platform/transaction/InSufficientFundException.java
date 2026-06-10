package com.banking.platform.transaction;

public class InSufficientFundException extends RuntimeException{
    public InSufficientFundException (String message) {
        super(message);
    }
}
