package com.stockwise.dao;

import com.stockwise.exception.ProductNotFoundException;
import com.stockwise.model.Purchase;
import com.stockwise.model.PurchaseItem;
import com.stockwise.util.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {
    public List<Purchase> getAllPurchases() throws SQLException {
        String sql = """
                SELECT p.purchase_id, p.supplier_id, s.supplier_name, p.purchase_date, p.total_amount
                FROM purchases p JOIN suppliers s ON p.supplier_id = s.supplier_id
                ORDER BY p.purchase_date DESC, p.purchase_id DESC
                """;
        List<Purchase> purchases = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                purchases.add(new Purchase(result.getInt("purchase_id"), result.getInt("supplier_id"),
                        result.getString("supplier_name"), result.getTimestamp("purchase_date").toLocalDateTime(),
                        result.getBigDecimal("total_amount"), List.of()));
            }
        }
        return purchases;
    }

    public Purchase createPurchase(int supplierId, int userId, List<PurchaseItem> items)
            throws SQLException, ProductNotFoundException {
        String productSql = "SELECT product_name, stock_quantity FROM products WHERE product_id = ? FOR UPDATE";
        String purchaseSql = "INSERT INTO purchases (supplier_id, user_id, total_amount) VALUES (?, ?, ?)";
        String itemSql = "INSERT INTO purchase_items (purchase_id, product_id, quantity, unit_cost) VALUES (?, ?, ?, ?)";
        String stockSql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
        String historySql = "INSERT INTO inventory_transactions (product_id, user_id, transaction_type, quantity, previous_stock, new_stock) VALUES (?, ?, 'PURCHASE', ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<PurchaseItem> receivedItems = new ArrayList<>();
                List<Integer> previousStocks = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;
                for (PurchaseItem requested : items) {
                    try (PreparedStatement statement = connection.prepareStatement(productSql)) {
                        statement.setInt(1, requested.getProductId());
                        try (ResultSet result = statement.executeQuery()) {
                            if (!result.next()) throw new ProductNotFoundException(requested.getProductId());
                            receivedItems.add(new PurchaseItem(requested.getProductId(), result.getString("product_name"),
                                    requested.getQuantity(), requested.getUnitCost()));
                            previousStocks.add(result.getInt("stock_quantity"));
                            total = total.add(requested.getSubtotal());
                        }
                    }
                }
                int purchaseId;
                try (PreparedStatement statement = connection.prepareStatement(purchaseSql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, supplierId); statement.setInt(2, userId); statement.setBigDecimal(3, total); statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Purchase ID could not be generated.");
                        purchaseId = keys.getInt(1);
                    }
                }
                for (int i = 0; i < receivedItems.size(); i++) {
                    PurchaseItem item = receivedItems.get(i); int previous = previousStocks.get(i);
                    try (PreparedStatement itemStatement = connection.prepareStatement(itemSql);
                         PreparedStatement stockStatement = connection.prepareStatement(stockSql);
                         PreparedStatement historyStatement = connection.prepareStatement(historySql)) {
                        itemStatement.setInt(1, purchaseId); itemStatement.setInt(2, item.getProductId());
                        itemStatement.setInt(3, item.getQuantity()); itemStatement.setBigDecimal(4, item.getUnitCost()); itemStatement.executeUpdate();
                        stockStatement.setInt(1, previous + item.getQuantity()); stockStatement.setInt(2, item.getProductId()); stockStatement.executeUpdate();
                        historyStatement.setInt(1, item.getProductId()); historyStatement.setInt(2, userId);
                        historyStatement.setInt(3, item.getQuantity()); historyStatement.setInt(4, previous);
                        historyStatement.setInt(5, previous + item.getQuantity()); historyStatement.executeUpdate();
                    }
                }
                connection.commit();
                return new Purchase(purchaseId, supplierId, null, LocalDateTime.now(), total, receivedItems);
            } catch (SQLException | ProductNotFoundException e) {
                connection.rollback(); throw e;
            } catch (RuntimeException e) { connection.rollback(); throw e; }
        }
    }
}
