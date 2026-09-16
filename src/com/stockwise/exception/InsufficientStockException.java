package com.stockwise.exception;

public class InsufficientStockException extends Exception {

    public InsufficientStockException(int availableStock, int requestedQuantity) {
        super("Insufficient stock. Available: " + availableStock
                + ", requested: " + requestedQuantity + ".");
    }
}
