package com.stockwise.model;

import java.math.BigDecimal;

public class TopSellingProduct {
    private final String productName;
    private final int unitsSold;
    private final BigDecimal salesAmount;
    public TopSellingProduct(String productName, int unitsSold, BigDecimal salesAmount) {
        this.productName = productName;
        this.unitsSold = unitsSold;
        this.salesAmount = salesAmount;
    }
    public String getProductName() { return productName; }
    public int getUnitsSold() { return unitsSold; }
    public BigDecimal getSalesAmount() { return salesAmount; }
}
