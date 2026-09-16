package com.stockwise.dao;

import com.stockwise.model.SalesSummary;
import com.stockwise.model.TopSellingProduct;
import com.stockwise.util.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    public SalesSummary getSalesSummary() throws SQLException {
        String sql = """
                SELECT COUNT(*) AS bill_count,
                       COALESCE(SUM(subtotal), 0) AS subtotal,
                       COALESCE(SUM(discount), 0) AS discount,
                       COALESCE(SUM(tax), 0) AS tax,
                       COALESCE(SUM(total_amount), 0) AS total_amount
                FROM bills WHERE status = 'COMPLETED'
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return new SalesSummary(result.getInt("bill_count"), result.getBigDecimal("subtotal"),
                    result.getBigDecimal("discount"), result.getBigDecimal("tax"), result.getBigDecimal("total_amount"));
        }
    }

    public BigDecimal getPurchaseTotal() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM purchases";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            result.next(); return result.getBigDecimal("total");
        }
    }

    public BigDecimal getInventoryValue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(price * stock_quantity), 0) AS total FROM products";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            result.next(); return result.getBigDecimal("total");
        }
    }

    public List<TopSellingProduct> getTopSellingProducts() throws SQLException {
        String sql = """
                SELECT p.product_name, SUM(bi.quantity) AS units_sold,
                       SUM(bi.subtotal) AS sales_amount
                FROM bill_items bi
                JOIN bills b ON bi.bill_id = b.bill_id
                JOIN products p ON bi.product_id = p.product_id
                WHERE b.status = 'COMPLETED'
                GROUP BY p.product_id, p.product_name
                ORDER BY units_sold DESC, sales_amount DESC
                LIMIT 5
                """;
        List<TopSellingProduct> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            while (result.next()) products.add(new TopSellingProduct(result.getString("product_name"),
                    result.getInt("units_sold"), result.getBigDecimal("sales_amount")));
        }
        return products;
    }
}
