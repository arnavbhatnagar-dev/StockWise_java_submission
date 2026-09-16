package com.stockwise.cli;

import com.stockwise.model.Product;
import com.stockwise.model.SalesSummary;
import com.stockwise.model.TopSellingProduct;
import com.stockwise.service.ReportService;
import java.util.List;
import java.util.Scanner;

public class ReportCLI {
    private final ReportService reportService = new ReportService();
    private final Scanner scanner;

    public ReportCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n====== REPORTS, ANALYTICS & ALERTS ======");
            System.out.println("1. Sales Summary");
            System.out.println("2. Top Selling Products");
            System.out.println("3. Purchase Summary");
            System.out.println("4. Low Stock Alerts");
            System.out.println("5. Inventory Valuation");
            System.out.println("9. Back");
            try {
                switch (readInt("Enter choice: ")) {
                    case 1 -> showSalesSummary();
                    case 2 -> showTopSellingProducts();
                    case 3 -> showPurchaseSummary();
                    case 4 -> showLowStockAlerts();
                    case 5 -> showInventoryValue();
                    case 9 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void showSalesSummary() throws Exception {
        SalesSummary summary = reportService.getSalesSummary();
        System.out.println("\n--- SALES SUMMARY ---");
        System.out.println("Completed bills: " + summary.getBillCount());
        System.out.printf("Subtotal: Rs. %.2f%n", summary.getSubtotal());
        System.out.printf("Discounts: Rs. %.2f%n", summary.getDiscount());
        System.out.printf("Tax collected: Rs. %.2f%n", summary.getTax());
        System.out.printf("Sales total: Rs. %.2f%n", summary.getTotalAmount());
    }

    private void showTopSellingProducts() throws Exception {
        List<TopSellingProduct> products = reportService.getTopSellingProducts();
        System.out.println("\n--- TOP SELLING PRODUCTS ---");
        if (products.isEmpty()) { System.out.println("No completed sales found."); return; }
        System.out.printf("%-25s %-12s %-12s%n", "Product", "Units Sold", "Sales");
        for (TopSellingProduct product : products) {
            System.out.printf("%-25s %-12d Rs. %.2f%n", product.getProductName(),
                    product.getUnitsSold(), product.getSalesAmount());
        }
    }

    private void showPurchaseSummary() throws Exception {
        System.out.println("\n--- PURCHASE SUMMARY ---");
        System.out.printf("Total purchase spending: Rs. %.2f%n", reportService.getPurchaseTotal());
    }

    private void showLowStockAlerts() throws Exception {
        List<Product> products = reportService.getLowStockProducts();
        System.out.println("\n--- LOW STOCK ALERTS ---");
        if (products.isEmpty()) { System.out.println("No low-stock alerts."); return; }
        for (Product product : products) System.out.println(product);
    }

    private void showInventoryValue() throws Exception {
        System.out.println("\n--- INVENTORY VALUATION ---");
        System.out.printf("Current inventory value: Rs. %.2f%n", reportService.getInventoryValue());
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }
}
