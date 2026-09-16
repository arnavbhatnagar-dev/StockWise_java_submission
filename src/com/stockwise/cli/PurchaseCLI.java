package com.stockwise.cli;

import com.stockwise.model.Purchase;
import com.stockwise.model.PurchaseItem;
import com.stockwise.model.Supplier;
import com.stockwise.service.PurchaseService;
import com.stockwise.model.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PurchaseCLI {
    private final PurchaseService purchaseService = new PurchaseService();
    private final Scanner scanner;
    private final User loggedInUser;
    public PurchaseCLI(Scanner scanner, User loggedInUser) {
        this.scanner = scanner;
        this.loggedInUser = loggedInUser;
    }
    public void start() {
        while (true) {
            System.out.println("\n====== SUPPLIER & PURCHASES ======");
            System.out.println("1. Add Supplier\n2. View Suppliers\n3. Receive Purchase\n4. Purchase History\n9. Back");
            try {
                switch (readInt("Enter choice: ")) {
                    case 1 -> addSupplier(); case 2 -> viewSuppliers(); case 3 -> receivePurchase(); case 4 -> viewPurchaseHistory(); case 9 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) { System.out.println("ERROR: " + e.getMessage()); }
        }
    }
    private void addSupplier() throws Exception {
        Supplier supplier = new Supplier(readString("Supplier name: "), readString("Phone: "),
                readString("Email: "), readString("Address: "));
        purchaseService.addSupplier(supplier); System.out.println("Supplier added successfully.");
    }
    private void viewSuppliers() throws Exception {
        List<Supplier> suppliers = purchaseService.getAllSuppliers();
        if (suppliers.isEmpty()) { System.out.println("No suppliers found."); return; }
        System.out.printf("%-5s %-25s %-16s %-25s%n", "ID", "Supplier", "Phone", "Email");
        for (Supplier s : suppliers) System.out.printf("%-5d %-25s %-16s %-25s%n", s.getSupplierId(), s.getSupplierName(), s.getPhone(), s.getEmail());
    }
    private void receivePurchase() throws Exception {
        viewSuppliers();
        int supplierId = readInt("Supplier ID: ");
        List<PurchaseItem> items = new ArrayList<>();
        while (true) {
            int productId = readInt("Product ID (0 to finish): "); if (productId == 0) break;
            int quantity = readInt("Quantity received: ");
            BigDecimal unitCost = readAmount("Unit cost: ");
            items.add(new PurchaseItem(productId, quantity, unitCost));
        }
        Purchase purchase = purchaseService.receivePurchase(supplierId, loggedInUser.getUserId(), items);
        System.out.printf("Purchase #%d recorded. Total: Rs. %.2f%n", purchase.getPurchaseId(), purchase.getTotalAmount());
    }
    private void viewPurchaseHistory() throws Exception {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        if (purchases.isEmpty()) { System.out.println("No purchases found."); return; }
        System.out.printf("%-9s %-25s %-20s %-12s%n", "Purchase", "Supplier", "Date", "Total");
        for (Purchase purchase : purchases) System.out.printf("%-9d %-25s %-20s Rs. %.2f%n",
                purchase.getPurchaseId(), purchase.getSupplierName(), purchase.getPurchaseDate(), purchase.getTotalAmount());
    }
    private int readInt(String prompt) { System.out.print(prompt); return Integer.parseInt(scanner.nextLine().trim()); }
    private BigDecimal readAmount(String prompt) { System.out.print(prompt); return new BigDecimal(scanner.nextLine().trim()); }
    private String readString(String prompt) { System.out.print(prompt); return scanner.nextLine().trim(); }
}
