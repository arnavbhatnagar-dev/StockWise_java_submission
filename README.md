# StockWise - Retail Billing, Inventory & Management System using Java

## 1. Project Overview

The **StockWise Retail Management System** is a command-line based Java application developed to manage the core operations of a small retail business.

The application provides functionality for managing products and inventory, generating customer bills, handling suppliers and purchases, and generating business reports and alerts.

The project demonstrates important concepts from the **Programming in Java** syllabus through a practical database-driven application using **Java, JDBC, and MySQL**.

---

## 2. Objectives

The main objectives of this project are:

- To develop a practical Java-based retail management application.
- To manage products and their inventory efficiently.
- To provide a complete billing and sales management system.
- To manage suppliers and stock purchases.
- To generate useful sales, inventory, and purchase reports.
- To demonstrate Object-Oriented Programming concepts in Java.
- To implement exception handling and input validation.
- To demonstrate Java Collections Framework.
- To implement file handling and application logging.
- To demonstrate multithreading through background inventory monitoring.
- To connect Java with a MySQL database using JDBC.
- To implement database transactions for reliable operations.
- To develop a modular and maintainable command-line application.

---

## 3. Main Features

### 3.1 Inventory & Product Management

The Inventory Management module provides the following operations:

- Add a new product.
- View all products.
- Search products.
- Update product information.
- Delete products.
- Manage product categories.
- Adjust stock quantity.
- Add stock and remove stock.
- Prevent stock from becoming negative.
- Configure minimum stock levels.
- Detect low-stock products.
- Maintain inventory transaction history.
- Record stock changes along with the responsible user.

---

### 3.2 Billing & Sales Management

The Billing and Sales module provides:

- Create customer bills.
- Add multiple products to a bill.
- Validate available stock before selling.
- Calculate item subtotals.
- Calculate bill subtotal.
- Apply discounts.
- Calculate tax.
- Calculate final bill amount.
- Support multiple payment methods.
- Generate bill/invoice information.
- View billing history.
- Cancel completed bills.
- Restore inventory when applicable during bill cancellation.
- Automatically update inventory after successful sales.
- Maintain payment records.

---

### 3.3 Supplier & Purchase Management

The Supplier and Purchase module provides:

- Add suppliers.
- Update supplier information.
- View suppliers.
- Search supplier information.
- Create purchase records.
- Add multiple products to purchases.
- Record product quantity purchased.
- Record unit purchase cost.
- Calculate purchase totals.
- Automatically increase inventory after receiving stock.
- Maintain purchase history.

---

### 3.4 Reports, Analytics & Alerts

The reporting module provides:

- Daily sales reports.
- Monthly sales reports.
- Sales summaries.
- Purchase reports.
- Inventory status reports.
- Inventory value information.
- Top-selling product analysis.
- Low-stock reports.
- Business-oriented summary information.
- Inventory monitoring and alerts.

---

## 4. Java Concepts Used

This project demonstrates concepts covered in the Programming in Java syllabus.

### Java Fundamentals

- Variables
- Data types
- Operators
- Expressions
- Input and output
- Conditional statements
- Loops
- `switch`
- `break`
- `continue`

### Object-Oriented Programming

- Classes and Objects
- Constructors
- Methods
- Encapsulation
- Inheritance
- Method Overriding
- Method Overloading
- Abstract classes
- Interfaces
- Polymorphism
- `this` keyword
- `super` keyword
- `final`
- Packages

### Exception Handling

- Exception handling
- `try-catch`
- `finally`
- `throw`
- `throws`
- Custom exceptions
- Input validation

Custom exceptions used in the project include:

- `ProductNotFoundException`
- `CategoryNotFoundException`
- `InvalidQuantityException`
- `InsufficientStockException`
- `BillNotFoundException`
- `SupplierNotFoundException`
- `InvalidCredentialsException`

### Collections

- Java Collections Framework
- `List`
- `ArrayList`
- Collection-based data processing

### File I/O

- File handling
- File streams
- Application logging
- Configuration file handling
- `Properties`

### Multithreading

- Thread creation
- Thread execution
- Background inventory monitoring

### JDBC

- JDBC API
- MySQL database connectivity
- `Connection`
- `PreparedStatement`
- `Statement`
- `ResultSet`
- SQL operations
- Parameterized queries
- Database transactions
- Commit and rollback
- Row-level locking where required

---

## 5. System Architecture

StockWise follows a layered architecture to separate user interaction, business logic, database operations, and data models.

```text
                         StockWise
                            |
                    +-------+-------+
                    |               |
                   CLI          Background
                 Interface       Monitor
                    |               |
                    v               |
                 Service Layer <-----+
                    |
                    v
                  DAO Layer
                    |
                    v
                  JDBC
                    |
                    v
                MySQL Database
