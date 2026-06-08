package com.banking.platform.customer;

public class CustomerNotFoundException extends  RuntimeException{
    public CustomerNotFoundException (String message) {
        super(message);
    }
}
