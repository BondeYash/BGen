package com.banking.platform.transaction;

public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException (String message) {
        super(message);
    }

}
