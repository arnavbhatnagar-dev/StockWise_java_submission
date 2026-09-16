package com.stockwise.service;

import com.stockwise.dao.BillingDAO;
import com.stockwise.enums.PaymentMethod;
import com.stockwise.exception.InsufficientStockException;
import com.stockwise.exception.BillNotFoundException;
import com.stockwise.exception.InvalidQuantityException;
import com.stockwise.exception.ProductNotFoundException;
import com.stockwise.model.Bill;
import com.stockwise.model.BillItem;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BillingService {
    private final BillingDAO billingDAO = new BillingDAO();

    public synchronized Bill checkout(int userId, List<BillItem> items,
                                      BigDecimal discount, BigDecimal taxRate,
                                      PaymentMethod paymentMethod)
            throws SQLException, ProductNotFoundException, InsufficientStockException,
            InvalidQuantityException {
        if (items == null || items.isEmpty()) throw new InvalidQuantityException("Add at least one item to the bill.");
        if (discount == null || discount.signum() < 0 || taxRate == null || taxRate.signum() < 0) {
            throw new IllegalArgumentException("Discount and tax rate cannot be negative.");
        }
        Map<Integer, Integer> quantities = new LinkedHashMap<>();
        for (BillItem item : items) {
            if (item.getQuantity() <= 0) throw new InvalidQuantityException("Item quantity must be greater than zero.");
            quantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        List<BillItem> consolidated = quantities.entrySet().stream()
                .map(entry -> new BillItem(entry.getKey(), entry.getValue())).toList();
        return billingDAO.createBill(userId, consolidated, discount, taxRate, paymentMethod);
    }

    public Bill getBillById(int billId) throws SQLException, BillNotFoundException {
        Bill bill = billingDAO.getBillById(billId);
        if (bill == null) throw new BillNotFoundException(billId);
        return bill;
    }

    public List<Bill> getAllBills() throws SQLException { return billingDAO.getAllBills(); }

    public synchronized void cancelBill(int billId, int userId)
            throws SQLException, BillNotFoundException {
        billingDAO.cancelBill(billId, userId);
    }
}
