package com.stockwise.model;

import java.math.BigDecimal;

public class BillItem {
    private final int productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;

    public BillItem(int productId, int quantity) {
        this(productId, null, quantity, null);
    }

    public BillItem(int productId, String productName, int quantity,
                    BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getSubtotal() {
        return unitPrice == null ? BigDecimal.ZERO
                : unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
