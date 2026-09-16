package com.stockwise.cli;

import com.stockwise.model.Product;
import com.stockwise.service.InventoryService;
import com.stockwise.model.Category;
import java.util.List;
import java.util.Scanner;
import com.stockwise.model.InventoryTransaction;
import com.stockwise.model.User;
public class InventoryCLI {

    private final InventoryService inventoryService;
    private final Scanner scanner;
    private final User loggedInUser;

    public InventoryCLI() {
        this(new Scanner(System.in), null);
    }

    public InventoryCLI(Scanner scanner) {
        this(scanner, null);
    }

    public InventoryCLI(Scanner scanner, User loggedInUser) {
        inventoryService = new InventoryService();
        this.scanner = scanner;
        this.loggedInUser = loggedInUser;
    }

    public void start() {

        while (true) {

            displayMenu();

            int choice = readInt("Enter choice: ");

            try {

                switch (choice) {

                    case 1 -> addProduct();

                    case 2 -> viewProducts();

                    case 3 -> searchProduct();

                    case 4 -> updateProduct();

                    case 5 -> deleteProduct();

                    case 6 -> adjustStock();

                    case 7 -> showLowStock();

                    case 8 -> showInventoryHistory();

                    case 9 -> {
                        System.out.println(
                                "\n ......Please wait.....\nRefirecting to main menu..."
                        );
                        return;
                    }

                    default ->
                            System.out.println(
                                    "\nInvalid choice."
                            );
                }

            } catch (Exception e) {

                System.out.println(
                        "\nERROR: " + e.getMessage()
                );
            }

            System.out.println();
        }
    }

    private void displayMenu() {

        System.out.println();
        System.out.println("======================================");
        System.out.println("       INVENTORY MANAGEMENT");
        System.out.println("======================================");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Search Product");
        System.out.println("4. Update Product");
        System.out.println("5. Delete Product");
        System.out.println("6. Adjust Stock");
        System.out.println("7. Low Stock Products");
        System.out.println("8. Inventory History");
        System.out.println("9. Back");
        System.out.println("======================================");
    }

    // =========================
    // ADD
    // =========================
    private void addProduct() throws Exception {

        System.out.println("\n--- Add Product ---");

        String name = readString("Product name: ");
        displayCategories();

int categoryId = readInt("Category ID: ");
        double price = readDouble("Price: ");
        int stock = readInt("Initial stock: ");
        int minimumStock = readInt("Minimum stock level: ");

        Product product = new Product(
                name,
                categoryId,
                price,
                stock,
                minimumStock
        );

        inventoryService.addProduct(product);

        System.out.println(
                "\nProduct added successfully."
        );
    }

    // =========================
    // VIEW
    // =========================
    private void viewProducts() throws Exception {

        System.out.println("\n--- All Products ---");

        List<Product> products =
                inventoryService.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (Product product : products) {
            System.out.println(product);
        }
    }

    // =========================
    // SEARCH
    // =========================
    private void searchProduct() throws Exception {

        System.out.println("\n--- Search Product ---");

        String keyword =
                readString("Enter product name: ");

        List<Product> products =
                inventoryService.searchProducts(keyword);

        if (products.isEmpty()) {
            System.out.println("No matching products found.");
            return;
        }

        for (Product product : products) {
            System.out.println(product);
        }
    }

    // =========================
    // UPDATE
    // =========================
    private void updateProduct() throws Exception {

        System.out.println("\n--- Update Product ---");

        int id = readInt("Product ID: ");

        Product existing = inventoryService.findProduct(id);

        System.out.println(
                "Current product: " + existing
        );

        String name =
                readString("New name: ");

        displayCategories();

        int categoryId =
                readInt("New category ID: ");

        double price =
                readDouble("New price: ");

        int minimumStock =
                readInt("New minimum stock: ");

        Product updated = new Product(
                id,
                name,
                categoryId,
                price,
                existing.getStockQuantity(),
                minimumStock
        );

        inventoryService.updateProduct(updated);

        System.out.println(
                "Product updated successfully."
        );
    }
    private void showInventoryHistory() throws Exception {

    System.out.println("\n--- INVENTORY HISTORY ---");

    List<com.stockwise.model.InventoryTransaction> transactions =
            inventoryService.getInventoryHistory();

    if (transactions.isEmpty()) {

        System.out.println("No inventory transactions found.");
        return;
    }

    System.out.println(
            "--------------------------------------------------------------------------"
    );

    System.out.printf(
            "%-5s %-8s %-15s %-10s %-12s %-12s%n",
            "ID",
            "Product",
            "Type",
            "Change",
            "Old Stock",
            "New Stock"
    );

    System.out.println(
            "--------------------------------------------------------------------------"
    );

    for (com.stockwise.model.InventoryTransaction transaction
            : transactions) {

        System.out.printf(
                "%-5d %-8d %-15s %-10d %-12d %-12d%n",
                transaction.getTransactionId(),
                transaction.getProductId(),
                transaction.getTransactionType(),
                transaction.getQuantity(),
                transaction.getPreviousStock(),
                transaction.getNewStock()
        );
    }

    System.out.println(
            "--------------------------------------------------------------------------"
    );
}

    // =========================
    // DELETE
    // =========================
    private void deleteProduct() throws Exception {

        System.out.println("\n--- Delete Product ---");

        int id =
                readInt("Product ID: ");

        Product product = inventoryService.findProduct(id);

        System.out.println(
                "You are deleting: " + product
        );

        String confirmation =
                readString("Type YES to confirm: ");

        if (!confirmation.equalsIgnoreCase("YES")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        inventoryService.deleteProduct(id);

        System.out.println(
                "Product deleted successfully."
        );
    }

    // =========================
    // STOCK ADJUSTMENT
    // =========================
    private void adjustStock() throws Exception {

        System.out.println("\n--- Adjust Stock ---");

        int productId =
                readInt("Product ID: ");

        Product product = inventoryService.findProduct(productId);

        System.out.println(
                "Current stock: " +
                        product.getStockQuantity()
        );

        System.out.println("\n1. Add Stock");
        System.out.println("2. Remove Stock");

        int choice =
                readInt("Choose operation: ");

        int quantity =
                readInt("Quantity: ");

        int change;
        String transactionType;

        if (choice == 1) {

            change = quantity;
            transactionType = "STOCK_IN";

        } else if (choice == 2) {

            change = -quantity;
            transactionType = "STOCK_OUT";

        } else {

            System.out.println("Invalid operation.");
            return;
        }

        if (quantity <= 0) {
            throw new com.stockwise.exception.InvalidQuantityException(
                    "Quantity must be greater than zero."
            );
        }

        int userId = loggedInUser == null ? 1 : loggedInUser.getUserId();

        inventoryService.adjustStock(
                productId,
                change,
                userId,
                transactionType
        );

        System.out.println(
                "Stock updated successfully."
        );
    }

    // =========================
    // LOW STOCK
    // =========================
    private void showLowStock() throws Exception {

        System.out.println("\n--- LOW STOCK PRODUCTS ---");

        List<Product> products =
                inventoryService.getLowStockProducts();

        if (products.isEmpty()) {

            System.out.println(
                    "No products are currently low on stock."
            );

            return;
        }

        for (Product product : products) {

            System.out.println(
                    product
            );
        }
    }

    private void displayCategories() throws Exception {

        System.out.println("\nAvailable Categories:");

        List<Category> categories = inventoryService.getAllCategories();

        for (Category category : categories) {
            System.out.println(
                    category.getCategoryId() + ". "
                            + category.getCategoryName()
            );
        }
    }

    // =========================
    // INPUT METHODS
    // =========================
    private int readInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid integer."
                );
            }
        }
    }

    private double readDouble(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Double.parseDouble(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    private String readString(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }
}
