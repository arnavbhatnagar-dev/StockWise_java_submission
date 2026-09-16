package com.stockwise.model;

public class Product {

    private int productId;
    private String productName;
    private int categoryId;
    private double price;
    private int stockQuantity;
    private int minimumStock;
    private String categoryName;

    // Constructor for creating a new product
    public Product(String productName, int categoryId, double price,
                   int stockQuantity, int minimumStock) {

        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
    }
    public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
}
public String getCategoryName() {
    return categoryName;
}

    // Constructor for products retrieved from database
    public Product(int productId, String productName, int categoryId,
                   double price, int stockQuantity, int minimumStock) {

        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
    }

    // Getters

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    // Setters

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

@Override
public String toString() {

    String category =
            categoryName == null
                    ? "Category ID: " + categoryId
                    : "Category: " + categoryName;

    return String.format(
            "ID: %d | %s | %s | Price: Rs. %.2f | Stock: %d | Minimum: %d",
            productId,
            productName,
            category,
            price,
            stockQuantity,
            minimumStock
    );
}
}