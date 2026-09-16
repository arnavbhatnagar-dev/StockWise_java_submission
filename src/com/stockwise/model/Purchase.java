package com.stockwise.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Purchase {
    private final int purchaseId;
    private final int supplierId;
    private final String supplierName;
    private final LocalDateTime purchaseDate;
    private final BigDecimal totalAmount;
    private final List<PurchaseItem> items;
    public Purchase(int purchaseId, int supplierId, String supplierName, LocalDateTime purchaseDate,
                    BigDecimal totalAmount, List<PurchaseItem> items) {
        this.purchaseId = purchaseId; this.supplierId = supplierId; this.supplierName = supplierName;
        this.purchaseDate = purchaseDate; this.totalAmount = totalAmount; this.items = List.copyOf(items);
    }
    public int getPurchaseId() { return purchaseId; }
    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<PurchaseItem> getItems() { return items; }
}
