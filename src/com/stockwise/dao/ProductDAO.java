package com.stockwise.dao;

import com.stockwise.exception.InsufficientStockException;
import com.stockwise.exception.ProductNotFoundException;
import com.stockwise.model.Product;
import com.stockwise.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.stockwise.model.InventoryTransaction;
public class ProductDAO {

    // =========================
    // ADD PRODUCT
    // =========================
    public void addProduct(Product product) throws SQLException {

        String sql = """
                INSERT INTO products
                (product_name, category_id, price, stock_quantity, minimum_stock)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getProductName());
            statement.setInt(2, product.getCategoryId());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStockQuantity());
            statement.setInt(5, product.getMinimumStock());

            statement.executeUpdate();
        }
    }

    // =========================
    // GET PRODUCT BY ID
    // =========================
    public Product getProductById(int productId) throws SQLException {

        String sql = """
                SELECT p.*, c.category_name
                FROM products p
                JOIN categories c ON p.category_id = c.category_id
                WHERE p.product_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                Product product = mapProduct(result);
                product.setCategoryName(result.getString("category_name"));
                return product;
            }
        }

        return null;
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================
    public List<Product> getAllProducts() throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = """
        SELECT p.*, c.category_name
        FROM products p
        JOIN categories c
            ON p.category_id = c.category_id
        ORDER BY p.product_id
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

    Product product = mapProduct(result);

    product.setCategoryName(
            result.getString("category_name")
    );

    products.add(product);
}
        }

        return products;
    }

    // =========================
    // SEARCH PRODUCTS
    // =========================
    public List<Product> searchProducts(String keyword) throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = """
        SELECT p.*, c.category_name
        FROM products p
        JOIN categories c
            ON p.category_id = c.category_id
        WHERE p.product_name LIKE ?
        ORDER BY p.product_id
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + keyword + "%");

            ResultSet result = statement.executeQuery();

while (result.next()) {

    Product product = mapProduct(result);

    product.setCategoryName(
            result.getString("category_name")
    );

    products.add(product);
}
        }

        return products;
    }

    // =========================
    // UPDATE PRODUCT
    // =========================
    public boolean updateProduct(Product product) throws SQLException {

        String sql = """
                UPDATE products
                SET product_name = ?,
                    category_id = ?,
                    price = ?,
                    minimum_stock = ?
                WHERE product_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getProductName());
            statement.setInt(2, product.getCategoryId());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getMinimumStock());
            statement.setInt(5, product.getProductId());

            return statement.executeUpdate() > 0;
        }
    }

    // =========================
    // DELETE PRODUCT
    // =========================
    public boolean deleteProduct(int productId) throws SQLException {

        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);

            return statement.executeUpdate() > 0;
        }
    }

    // =========================
    // ADJUST STOCK
    // =========================
    public void adjustStock(
            int productId,
            int quantityChange,
            int userId,
            String transactionType) throws SQLException,
            ProductNotFoundException, InsufficientStockException {

        String selectSql = """
                SELECT stock_quantity
                FROM products
                WHERE product_id = ?
                FOR UPDATE
                """;

        String updateSql = """
                UPDATE products
                SET stock_quantity = ?
                WHERE product_id = ?
                """;

        String historySql = """
                INSERT INTO inventory_transactions
                (product_id, user_id, transaction_type,
                 quantity, previous_stock, new_stock)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {

            try {
                connection.setAutoCommit(false);

                int previousStock;

                // Lock the product row
                try (PreparedStatement statement =
                             connection.prepareStatement(selectSql)) {

                    statement.setInt(1, productId);

                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new ProductNotFoundException(productId);
                        }

                        previousStock = result.getInt("stock_quantity");
                    }
                }

                int newStock = previousStock + quantityChange;

                if (newStock < 0) {
                    throw new InsufficientStockException(
                            previousStock, Math.abs(quantityChange)
                    );
                }

                // Update stock
                try (PreparedStatement statement =
                             connection.prepareStatement(updateSql)) {

                    statement.setInt(1, newStock);
                    statement.setInt(2, productId);

                    statement.executeUpdate();
                }

                // Record transaction
                try (PreparedStatement statement =
                             connection.prepareStatement(historySql)) {

                    statement.setInt(1, productId);
                    statement.setInt(2, userId);
                    statement.setString(3, transactionType);
                    statement.setInt(4, quantityChange);
                    statement.setInt(5, previousStock);
                    statement.setInt(6, newStock);

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException | ProductNotFoundException
                     | InsufficientStockException e) {

                connection.rollback();
                throw e;
            }
        }
    }

    // =========================
    // LOW STOCK PRODUCTS
    // =========================
    public List<Product> getLowStockProducts() throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT p.*, c.category_name
                FROM products p
                JOIN categories c ON p.category_id = c.category_id
                WHERE p.stock_quantity <= p.minimum_stock
                ORDER BY p.stock_quantity, p.product_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Product product = mapProduct(result);
                product.setCategoryName(result.getString("category_name"));
                products.add(product);
            }
        }

        return products;
    }

    // =========================
    // MAP DATABASE ROW → PRODUCT
    // =========================
    private Product mapProduct(ResultSet result) throws SQLException {

        return new Product(
                result.getInt("product_id"),
                result.getString("product_name"),
                result.getInt("category_id"),
                result.getDouble("price"),
                result.getInt("stock_quantity"),
                result.getInt("minimum_stock")
        );
    }
// =========================
// INVENTORY HISTORY
// =========================
public List<InventoryTransaction> getInventoryHistory()
        throws SQLException {

    List<InventoryTransaction> transactions =
            new ArrayList<>();

    String sql = """
            SELECT *
            FROM inventory_transactions
            ORDER BY transaction_date DESC
            """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql);
         ResultSet result = statement.executeQuery()) {

        while (result.next()) {

            InventoryTransaction transaction =
                    new InventoryTransaction(
                            result.getInt("transaction_id"),
                            result.getInt("product_id"),
                            result.getInt("user_id"),
                            result.getString("transaction_type"),
                            result.getInt("quantity"),
                            result.getInt("previous_stock"),
                            result.getInt("new_stock"),
                            result.getTimestamp("transaction_date")
                                    .toLocalDateTime()
                    );

            transactions.add(transaction);
        }
    }

    return transactions;
}}
