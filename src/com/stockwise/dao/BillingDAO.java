package com.stockwise.dao;

import com.stockwise.enums.PaymentMethod;
import com.stockwise.exception.InsufficientStockException;
import com.stockwise.exception.ProductNotFoundException;
import com.stockwise.model.Bill;
import com.stockwise.model.BillItem;
import com.stockwise.util.DBConnection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {
    public void cancelBill(int billId, int userId)
            throws SQLException, com.stockwise.exception.BillNotFoundException {
        String billSql = "SELECT status FROM bills WHERE bill_id = ? FOR UPDATE";
        String itemSql = """
                SELECT bi.product_id, bi.quantity, p.stock_quantity
                FROM bill_items bi JOIN products p ON bi.product_id = p.product_id
                WHERE bi.bill_id = ? FOR UPDATE
                """;
        String stockSql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
        String historySql = "INSERT INTO inventory_transactions (product_id, user_id, transaction_type, quantity, previous_stock, new_stock) VALUES (?, ?, 'SALE_CANCEL', ?, ?, ?)";
        String statusSql = "UPDATE bills SET status = 'CANCELLED' WHERE bill_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement billStatement = connection.prepareStatement(billSql)) {
                billStatement.setInt(1, billId);
                try (ResultSet billResult = billStatement.executeQuery()) {
                    if (!billResult.next()) {
                        throw new com.stockwise.exception.BillNotFoundException(billId);
                    }
                    if ("CANCELLED".equalsIgnoreCase(billResult.getString("status"))) {
                        throw new IllegalStateException("This bill has already been cancelled.");
                    }
                }
                try (PreparedStatement itemStatement = connection.prepareStatement(itemSql)) {
                    itemStatement.setInt(1, billId);
                    try (ResultSet itemResult = itemStatement.executeQuery()) {
                        while (itemResult.next()) {
                            int productId = itemResult.getInt("product_id");
                            int quantity = itemResult.getInt("quantity");
                            int previousStock = itemResult.getInt("stock_quantity");
                            try (PreparedStatement stockStatement = connection.prepareStatement(stockSql);
                                 PreparedStatement historyStatement = connection.prepareStatement(historySql)) {
                                stockStatement.setInt(1, previousStock + quantity);
                                stockStatement.setInt(2, productId);
                                stockStatement.executeUpdate();
                                historyStatement.setInt(1, productId);
                                historyStatement.setInt(2, userId);
                                historyStatement.setInt(3, quantity);
                                historyStatement.setInt(4, previousStock);
                                historyStatement.setInt(5, previousStock + quantity);
                                historyStatement.executeUpdate();
                            }
                        }
                    }
                }
                try (PreparedStatement statusStatement = connection.prepareStatement(statusSql)) {
                    statusStatement.setInt(1, billId);
                    statusStatement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException | com.stockwise.exception.BillNotFoundException e) {
                connection.rollback();
                throw e;
            } catch (RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public Bill getBillById(int billId) throws SQLException {
        String billSql = """
                SELECT b.*, p.payment_method
                FROM bills b LEFT JOIN payments p ON b.bill_id = p.bill_id
                WHERE b.bill_id = ?
                """;
        String itemsSql = """
                SELECT bi.product_id, pr.product_name, bi.quantity, bi.unit_price
                FROM bill_items bi JOIN products pr ON bi.product_id = pr.product_id
                WHERE bi.bill_id = ? ORDER BY bi.bill_item_id
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement billStatement = connection.prepareStatement(billSql)) {
            billStatement.setInt(1, billId);
            try (ResultSet result = billStatement.executeQuery()) {
                if (!result.next()) return null;
                List<BillItem> items = new ArrayList<>();
                try (PreparedStatement itemStatement = connection.prepareStatement(itemsSql)) {
                    itemStatement.setInt(1, billId);
                    try (ResultSet itemResult = itemStatement.executeQuery()) {
                        while (itemResult.next()) items.add(new BillItem(
                                itemResult.getInt("product_id"), itemResult.getString("product_name"),
                                itemResult.getInt("quantity"), itemResult.getBigDecimal("unit_price")));
                    }
                }
                return mapBill(result, items);
            }
        }
    }

    public List<Bill> getAllBills() throws SQLException {
        String sql = """
                SELECT b.*, p.payment_method FROM bills b
                LEFT JOIN payments p ON b.bill_id = p.bill_id
                ORDER BY b.bill_date DESC, b.bill_id DESC
                """;
        List<Bill> bills = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) bills.add(mapBill(result, List.of()));
        }
        return bills;
    }
    public Bill createBill(int userId, List<BillItem> requestedItems,
                           BigDecimal discount, BigDecimal taxRate,
                           PaymentMethod paymentMethod)
            throws SQLException, ProductNotFoundException, InsufficientStockException {
        String productSql = "SELECT product_name, price, stock_quantity FROM products WHERE product_id = ? FOR UPDATE";
        String billSql = "INSERT INTO bills (user_id, subtotal, discount, tax, total_amount, status) VALUES (?, ?, ?, ?, ?, 'COMPLETED')";
        String itemSql = "INSERT INTO bill_items (bill_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        String stockSql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
        String inventorySql = "INSERT INTO inventory_transactions (product_id, user_id, transaction_type, quantity, previous_stock, new_stock) VALUES (?, ?, 'SALE', ?, ?, ?)";
        String paymentSql = "INSERT INTO payments (bill_id, payment_method, amount) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<BillItem> pricedItems = new ArrayList<>();
                List<Integer> previousStocks = new ArrayList<>();
                BigDecimal subtotal = BigDecimal.ZERO;
                for (BillItem requested : requestedItems) {
                    try (PreparedStatement statement = connection.prepareStatement(productSql)) {
                        statement.setInt(1, requested.getProductId());
                        try (ResultSet result = statement.executeQuery()) {
                            if (!result.next()) throw new ProductNotFoundException(requested.getProductId());
                            int available = result.getInt("stock_quantity");
                            if (available < requested.getQuantity()) {
                                throw new InsufficientStockException(available, requested.getQuantity());
                            }
                            BillItem item = new BillItem(requested.getProductId(),
                                    result.getString("product_name"), requested.getQuantity(),
                                    result.getBigDecimal("price"));
                            pricedItems.add(item);
                            previousStocks.add(available);
                            subtotal = subtotal.add(item.getSubtotal());
                        }
                    }
                }
                if (discount.compareTo(subtotal) > 0) {
                    throw new IllegalArgumentException(
                            "Discount cannot exceed the bill subtotal."
                    );
                }
                BigDecimal taxableAmount = subtotal.subtract(discount);
                BigDecimal tax = taxableAmount.multiply(taxRate)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal total = taxableAmount.add(tax);
                int billId;
                try (PreparedStatement statement = connection.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, userId); statement.setBigDecimal(2, subtotal);
                    statement.setBigDecimal(3, discount); statement.setBigDecimal(4, tax);
                    statement.setBigDecimal(5, total); statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Bill ID could not be generated.");
                        billId = keys.getInt(1);
                    }
                }
                for (int i = 0; i < pricedItems.size(); i++) {
                    BillItem item = pricedItems.get(i);
                    int previous = previousStocks.get(i);
                    try (PreparedStatement statement = connection.prepareStatement(itemSql)) {
                        statement.setInt(1, billId); statement.setInt(2, item.getProductId());
                        statement.setInt(3, item.getQuantity()); statement.setBigDecimal(4, item.getUnitPrice());
                        statement.setBigDecimal(5, item.getSubtotal()); statement.executeUpdate();
                    }
                    try (PreparedStatement statement = connection.prepareStatement(stockSql)) {
                        statement.setInt(1, previous - item.getQuantity()); statement.setInt(2, item.getProductId());
                        statement.executeUpdate();
                    }
                    try (PreparedStatement statement = connection.prepareStatement(inventorySql)) {
                        statement.setInt(1, item.getProductId()); statement.setInt(2, userId);
                        statement.setInt(3, -item.getQuantity()); statement.setInt(4, previous);
                        statement.setInt(5, previous - item.getQuantity()); statement.executeUpdate();
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(paymentSql)) {
                    statement.setInt(1, billId); statement.setString(2, paymentMethod.name());
                    statement.setBigDecimal(3, total); statement.executeUpdate();
                }
                connection.commit();
                return new Bill(billId, userId, LocalDateTime.now(), subtotal, discount,
                        tax, total, "COMPLETED", paymentMethod, pricedItems);
            } catch (SQLException | ProductNotFoundException | InsufficientStockException e) {
                connection.rollback();
                throw e;
            } catch (RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private Bill mapBill(ResultSet result, List<BillItem> items) throws SQLException {
        Timestamp timestamp = result.getTimestamp("bill_date");
        String method = result.getString("payment_method");
        return new Bill(result.getInt("bill_id"), result.getInt("user_id"),
                timestamp == null ? null : timestamp.toLocalDateTime(),
                result.getBigDecimal("subtotal"), result.getBigDecimal("discount"),
                result.getBigDecimal("tax"), result.getBigDecimal("total_amount"),
                result.getString("status"), method == null ? null : PaymentMethod.valueOf(method), items);
    }
}
