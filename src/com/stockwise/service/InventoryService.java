package com.stockwise.service;

import com.stockwise.dao.ProductDAO;
import com.stockwise.exception.CategoryNotFoundException;
import com.stockwise.exception.InvalidQuantityException;
import com.stockwise.exception.ProductNotFoundException;
import com.stockwise.exception.InsufficientStockException;
import com.stockwise.model.Product;
import com.stockwise.dao.CategoryDAO;
import com.stockwise.model.Category;
import java.sql.SQLException;
import java.util.List;
import com.stockwise.model.InventoryTransaction;
public class InventoryService {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

public InventoryService() {
    productDAO = new ProductDAO();
    categoryDAO = new CategoryDAO();
}
    // Add product
 public void addProduct(Product product) throws SQLException,
         CategoryNotFoundException, InvalidQuantityException {

    validateProduct(product);

    if (!categoryDAO.categoryExists(product.getCategoryId())) {
        throw new CategoryNotFoundException(product.getCategoryId());
    }

    productDAO.addProduct(product);
}
    // Find product
    public Product findProduct(int productId) throws SQLException,
            ProductNotFoundException {

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }
        return product;
    }

    // Get all products
    public List<Product> getAllProducts() throws SQLException {

        return productDAO.getAllProducts();
    }

    // Search
    public List<Product> searchProducts(String keyword)
            throws SQLException {

        return productDAO.searchProducts(keyword);
    }

    // Update
public void updateProduct(Product product)
        throws SQLException, CategoryNotFoundException,
        ProductNotFoundException, InvalidQuantityException {

    validateProduct(product);

    if (!categoryDAO.categoryExists(product.getCategoryId())) {
        throw new CategoryNotFoundException(product.getCategoryId());
    }

    if (!productDAO.updateProduct(product)) {
        throw new ProductNotFoundException(product.getProductId());
    }
}
public List<Category> getAllCategories()
        throws SQLException {

    return categoryDAO.getAllCategories();
}

public boolean categoryExists(int categoryId)
        throws SQLException {

    return categoryDAO.categoryExists(categoryId);
}
    // Delete
    public void deleteProduct(int productId)
            throws SQLException, ProductNotFoundException {

        if (!productDAO.deleteProduct(productId)) {
            throw new ProductNotFoundException(productId);
        }
    }

    // Stock adjustment
    public synchronized void adjustStock(
            int productId,
            int quantityChange,
            int userId,
            String transactionType)
            throws SQLException, InvalidQuantityException,
            ProductNotFoundException, InsufficientStockException {

        if (quantityChange == 0) {
            throw new InvalidQuantityException(
                    "Stock change cannot be zero."
            );
        }

        productDAO.adjustStock(
                productId,
                quantityChange,
                userId,
                transactionType
        );
    }
    public List<InventoryTransaction> getInventoryHistory()
        throws SQLException {

    return productDAO.getInventoryHistory();
}
    // Low stock
    public List<Product> getLowStockProducts()
            throws SQLException {

        return productDAO.getLowStockProducts();
    }

    // Validation
    private void validateProduct(Product product)
            throws InvalidQuantityException {

        if (product.getProductName() == null ||
                product.getProductName().isBlank()) {

            throw new IllegalArgumentException(
                    "Product name cannot be empty."
            );
        }

        if (product.getPrice() < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be negative."
            );
        }

        if (product.getStockQuantity() < 0) {
            throw new InvalidQuantityException(
                    "Stock quantity cannot be negative."
            );
        }

        if (product.getMinimumStock() < 0) {
            throw new InvalidQuantityException(
                    "Minimum stock cannot be negative."
            );
        }
    }
}
