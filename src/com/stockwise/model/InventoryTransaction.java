package com.stockwise.model;

import java.time.LocalDateTime;

public class InventoryTransaction {

    private int transactionId;
    private int productId;
    private int userId;
    private String transactionType;
    private int quantity;
    private int previousStock;
    private int newStock;
    private LocalDateTime transactionDate;

    public InventoryTransaction(
            int transactionId,
            int productId,
            int userId,
            String transactionType,
            int quantity,
            int previousStock,
            int newStock,
            LocalDateTime transactionDate) {

        this.transactionId = transactionId;
        this.productId = productId;
        this.userId = userId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getProductId() {
        return productId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPreviousStock() {
        return previousStock;
    }

    public int getNewStock() {
        return newStock;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}