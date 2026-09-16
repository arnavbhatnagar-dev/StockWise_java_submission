package com.stockwise.exception;

public class BillNotFoundException extends Exception {
    public BillNotFoundException(int billId) {
        super("Bill with ID " + billId + " was not found.");
    }
}
