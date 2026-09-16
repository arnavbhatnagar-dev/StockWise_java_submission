package com.stockwise.model;

import com.stockwise.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Bill {
    private final int billId;
    private final int userId;
    private final LocalDateTime billDate;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal tax;
    private final BigDecimal totalAmount;
    private final String status;
    private final PaymentMethod paymentMethod;
    private final List<BillItem> items;

    public Bill(int billId, int userId, LocalDateTime billDate,
                BigDecimal subtotal, BigDecimal discount, BigDecimal tax,
                BigDecimal totalAmount, String status,
                PaymentMethod paymentMethod, List<BillItem> items) {
        this.billId = billId;
        this.userId = userId;
        this.billDate = billDate;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.items = List.copyOf(items);
    }

    public int getBillId() { return billId; }
    public int getUserId() { return userId; }
    public LocalDateTime getBillDate() { return billDate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public List<BillItem> getItems() { return items; }
}
