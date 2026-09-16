package com.stockwise;

import com.stockwise.cli.BillingCLI;
import com.stockwise.cli.InventoryCLI;
import com.stockwise.cli.PurchaseCLI;
import com.stockwise.cli.ReportCLI;
import com.stockwise.model.User;
import com.stockwise.service.AuthService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n======================================");
        System.out.println("            STOCKWISE");
        System.out.println("     Retail Management System");
        System.out.println("======================================");

        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        while (true) {
            User user = login(scanner, authService);
            if (user == null) {
                System.out.println("\nThank you for using StockWise.");
                return;
            }
            runMainMenu(scanner, user);
        }
    }

    private static User login(Scanner scanner, AuthService authService) {
        while (true) {
            System.out.println("\n--- LOGIN ---");
            System.out.print("Username (or EXIT): ");
            String username = scanner.nextLine().trim();
            if ("EXIT".equalsIgnoreCase(username)) return null;
            System.out.print("Password: ");
            String password = scanner.nextLine();
            try {
                User user = authService.login(username, password);
                System.out.println("Welcome, " + user.getFullName() + " (" + user.getRole() + ").");
                return user;
            } catch (Exception e) {
                System.out.println("LOGIN FAILED: " + e.getMessage());
            }
        }
    }

    private static void runMainMenu(Scanner scanner, User user) {
        while (true) {
            System.out.println("\n1. Inventory & Product Management");
            System.out.println("2. Billing & Sales Management");
            System.out.println("3. Supplier & Purchase Management");
            System.out.println("4. Reports, Analytics & Alerts");
            System.out.println("8. Logout");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> openInventory(scanner, user);
                case "2" -> openBilling(scanner, user);
                case "3" -> openPurchases(scanner, user);
                case "4" -> openReports(scanner, user);
                case "8" -> { System.out.println("Logged out."); return; }
                case "0" -> { System.out.println("\nThank you for using StockWise."); System.exit(0); }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void openInventory(Scanner scanner, User user) {
        if (!user.getRole().canUseInventory()) { accessDenied(); return; }
        new InventoryCLI(scanner, user).start();
    }

    private static void openBilling(Scanner scanner, User user) {
        if (!user.getRole().canUseBilling()) { accessDenied(); return; }
        new BillingCLI(scanner, user).start();
    }

    private static void openPurchases(Scanner scanner, User user) {
        if (!user.getRole().canUsePurchases()) { accessDenied(); return; }
        new PurchaseCLI(scanner, user).start();
    }

    private static void openReports(Scanner scanner, User user) {
        if (!user.getRole().canUseReports()) { accessDenied(); return; }
        new ReportCLI(scanner).start();
    }

    private static void accessDenied() {
        System.out.println("Access denied for your role.");
    }
}
