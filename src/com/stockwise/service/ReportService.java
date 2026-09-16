package com.stockwise.service;

import com.stockwise.dao.ReportDAO;
import com.stockwise.model.Product;
import com.stockwise.model.SalesSummary;
import com.stockwise.model.TopSellingProduct;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ReportService {
    private final ReportDAO reportDAO = new ReportDAO();
    private final InventoryService inventoryService = new InventoryService();
    public SalesSummary getSalesSummary() throws SQLException { return reportDAO.getSalesSummary(); }
    public BigDecimal getPurchaseTotal() throws SQLException { return reportDAO.getPurchaseTotal(); }
    public BigDecimal getInventoryValue() throws SQLException { return reportDAO.getInventoryValue(); }
    public List<TopSellingProduct> getTopSellingProducts() throws SQLException { return reportDAO.getTopSellingProducts(); }
    public List<Product> getLowStockProducts() throws SQLException { return inventoryService.getLowStockProducts(); }
}
