package com.banking.platform.ledger;

public class UnbalancedLedgerException extends RuntimeException {
    public UnbalancedLedgerException(String message) {
        super(message);
    }
}
