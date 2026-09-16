package com.stockwise.model;

import java.math.BigDecimal;

public class SalesSummary {
    private final int billCount;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal tax;
    private final BigDecimal totalAmount;

    public SalesSummary(int billCount, BigDecimal subtotal, BigDecimal discount,
                        BigDecimal tax, BigDecimal totalAmount) {
        this.billCount = billCount;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.totalAmount = totalAmount;
    }
    public int getBillCount() { return billCount; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
