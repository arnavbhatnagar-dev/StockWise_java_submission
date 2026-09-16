package com.stockwise.exception;

public class SupplierNotFoundException extends Exception {
    public SupplierNotFoundException(int supplierId) {
        super("Supplier with ID " + supplierId + " was not found or is inactive.");
    }
}
