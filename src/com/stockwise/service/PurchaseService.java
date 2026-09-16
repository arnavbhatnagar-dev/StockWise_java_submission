package com.stockwise.service;

import com.stockwise.dao.PurchaseDAO;
import com.stockwise.dao.SupplierDAO;
import com.stockwise.exception.InvalidQuantityException;
import com.stockwise.exception.ProductNotFoundException;
import com.stockwise.exception.SupplierNotFoundException;
import com.stockwise.model.Purchase;
import com.stockwise.model.PurchaseItem;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PurchaseService {
    private final PurchaseDAO purchaseDAO = new PurchaseDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    public void addSupplier(com.stockwise.model.Supplier supplier) throws SQLException {
        if (supplier.getSupplierName() == null || supplier.getSupplierName().isBlank()) throw new IllegalArgumentException("Supplier name cannot be empty.");
        supplierDAO.addSupplier(supplier);
    }
    public List<com.stockwise.model.Supplier> getAllSuppliers() throws SQLException { return supplierDAO.getAllSuppliers(); }
    public List<Purchase> getAllPurchases() throws SQLException { return purchaseDAO.getAllPurchases(); }
    public synchronized Purchase receivePurchase(int supplierId, int userId, List<PurchaseItem> items)
            throws SQLException, SupplierNotFoundException, ProductNotFoundException, InvalidQuantityException {
        if (!supplierDAO.supplierExists(supplierId)) throw new SupplierNotFoundException(supplierId);
        if (items == null || items.isEmpty()) throw new InvalidQuantityException("Add at least one purchase item.");
        Map<Integer, PurchaseItem> consolidated = new LinkedHashMap<>();
        for (PurchaseItem item : items) {
            if (item.getQuantity() <= 0 || item.getUnitCost().signum() < 0) throw new InvalidQuantityException("Purchase quantity must be positive and unit cost cannot be negative.");
            PurchaseItem prior = consolidated.get(item.getProductId());
            consolidated.put(item.getProductId(), prior == null ? item : new PurchaseItem(item.getProductId(), prior.getQuantity() + item.getQuantity(), item.getUnitCost()));
        }
        return purchaseDAO.createPurchase(supplierId, userId, List.copyOf(consolidated.values()));
    }
}
