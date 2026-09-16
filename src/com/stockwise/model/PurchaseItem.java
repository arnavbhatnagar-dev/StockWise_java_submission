package com.stockwise.model;

import java.math.BigDecimal;

public class PurchaseItem {
    private final int productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitCost;
    public PurchaseItem(int productId, int quantity, BigDecimal unitCost) {
        this(productId, null, quantity, unitCost);
    }
    public PurchaseItem(int productId, String productName, int quantity, BigDecimal unitCost) {
        this.productId = productId; this.productName = productName; this.quantity = quantity; this.unitCost = unitCost;
    }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitCost() { return unitCost; }
    public BigDecimal getSubtotal() { return unitCost.multiply(BigDecimal.valueOf(quantity)); }
}
