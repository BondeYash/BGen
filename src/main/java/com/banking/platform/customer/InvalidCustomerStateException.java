package com.banking.platform.customer;

public class InvalidCustomerStateException extends RuntimeException {
    public InvalidCustomerStateException (String message) {
        super(message);
    }
}
