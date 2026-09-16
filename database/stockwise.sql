CREATE DATABASE IF NOT EXISTS stockwise;
USE stockwise;

-- =========================================
-- USERS
-- =========================================

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- CATEGORIES
-- =========================================

CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- =========================================
-- PRODUCTS
-- =========================================

CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    minimum_stock INT NOT NULL DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
);

-- =========================================
-- SUPPLIERS
-- =========================================

CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
);

-- =========================================
-- PURCHASES
-- =========================================

CREATE TABLE purchases (
    purchase_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_id INT NOT NULL,
    user_id INT NOT NULL,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,

    FOREIGN KEY (supplier_id)
        REFERENCES suppliers(supplier_id),

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

-- =========================================
-- PURCHASE ITEMS
-- =========================================

CREATE TABLE purchase_items (
    purchase_item_id INT PRIMARY KEY AUTO_INCREMENT,
    purchase_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (purchase_id)
        REFERENCES purchases(purchase_id),

    FOREIGN KEY (product_id)
        REFERENCES products(product_id)
);

-- =========================================
-- BILLS
-- =========================================

CREATE TABLE bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    tax DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

-- =========================================
-- BILL ITEMS
-- =========================================

CREATE TABLE bill_items (
    bill_item_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (bill_id)
        REFERENCES bills(bill_id),

    FOREIGN KEY (product_id)
        REFERENCES products(product_id)
);

-- =========================================
-- PAYMENTS
-- =========================================

CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (bill_id)
        REFERENCES bills(bill_id)
);

-- =========================================
-- INVENTORY TRANSACTIONS
-- =========================================

CREATE TABLE inventory_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    user_id INT,
    transaction_type VARCHAR(30) NOT NULL,
    quantity INT NOT NULL,
    previous_stock INT NOT NULL,
    new_stock INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id)
        REFERENCES products(product_id),

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

-- =========================================
-- AUDIT LOGS
-- =========================================

CREATE TABLE audit_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);


-- =========================================
-- DEMO CATEGORIES
-- =========================================

INSERT INTO categories
(category_name, description)
VALUES
('Grocery', 'Food and household grocery items'),
('Electronics', 'Electronic devices and accessories'),
('Personal Care', 'Personal hygiene and care products'),
('Stationery', 'Office and educational stationery');


-- =========================================
-- DEMO PRODUCTS
-- =========================================

INSERT INTO products
(product_name, category_id, price, stock_quantity, minimum_stock)
VALUES
('Rice 5kg', 1, 350.00, 25, 5),
('Milk 1L', 1, 60.00, 40, 10),
('USB Cable', 2, 250.00, 15, 5),
('Wireless Mouse', 2, 700.00, 8, 3),
('Shampoo 180ml', 3, 220.00, 20, 5),
('Notebook', 4, 80.00, 50, 10);


-- =========================================
-- DEMO USERS
-- =========================================

INSERT INTO users
(username, password, full_name, role)
VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN'),
('cashier', 'cashier123', 'Main Cashier', 'CASHIER'),
('manager', 'manager123', 'Inventory Manager', 'INVENTORY_MANAGER');