package com.stockwise.cli;

import com.stockwise.enums.PaymentMethod;
import com.stockwise.model.Bill;
import com.stockwise.model.BillItem;
import com.stockwise.service.BillingService;
import com.stockwise.model.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillingCLI {
    private final BillingService billingService = new BillingService();
    private final Scanner scanner;
    private final User loggedInUser;

    public BillingCLI(Scanner scanner, User loggedInUser) {
        this.scanner = scanner;
        this.loggedInUser = loggedInUser;
    }

    public void start() {
        while (true) {
            System.out.println("\n========== BILLING & SALES ==========");
            System.out.println("1. Create Bill\n2. View Bill\n3. View Sales\n4. Cancel Bill\n9. Back");
            try {
                int choice = readInt("Enter choice: ");
                if (choice == 9) return;
                switch (choice) {
                    case 1 -> createBill();
                    case 2 -> printReceipt(billingService.getBillById(readInt("Bill ID: ")));
                    case 3 -> viewSales();
                    case 4 -> cancelBill();
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) { System.out.println("ERROR: " + e.getMessage()); }
        }
    }

    private void createBill() throws Exception {
        List<BillItem> items = new ArrayList<>();
        System.out.println("\n--- Create Bill ---");
        while (true) {
            int productId = readInt("Product ID (0 to finish): ");
            if (productId == 0) break;
            items.add(new BillItem(productId, readInt("Quantity: ")));
        }
        BigDecimal discount = readAmount("Discount amount: ");
        BigDecimal taxRate = readAmount("Tax rate (%): ");
        PaymentMethod paymentMethod = readPaymentMethod();
        Bill bill = billingService.checkout(loggedInUser.getUserId(), items, discount, taxRate, paymentMethod);
        printReceipt(bill);
    }

    private PaymentMethod readPaymentMethod() {
        while (true) {
            System.out.print("Payment method (CASH/CARD/UPI): ");
            try { return PaymentMethod.valueOf(scanner.nextLine().trim().toUpperCase()); }
            catch (IllegalArgumentException e) { System.out.println("Choose CASH, CARD, or UPI."); }
        }
    }
    private void printReceipt(Bill bill) {
        System.out.println("\n========== STOCKWISE RECEIPT ==========");
        System.out.println("Bill #: " + bill.getBillId());
        for (BillItem item : bill.getItems()) {
            System.out.printf("%-20s %3d x Rs. %-8.2f Rs. %.2f%n", item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
        }
        System.out.printf("Subtotal: Rs. %.2f%nDiscount: Rs. %.2f%nTax: Rs. %.2f%nTOTAL: Rs. %.2f%nPayment: %s%n",
                bill.getSubtotal(), bill.getDiscount(), bill.getTax(), bill.getTotalAmount(), bill.getPaymentMethod());
    }
    private void viewSales() throws Exception {
        List<Bill> bills = billingService.getAllBills();
        if (bills.isEmpty()) { System.out.println("No sales found."); return; }
        System.out.println("\n--- SALES ---");
        System.out.printf("%-6s %-20s %-12s %-10s %-10s%n", "Bill", "Date", "Total", "Payment", "Status");
        for (Bill bill : bills) System.out.printf("%-6d %-20s Rs. %-8.2f %-10s %-10s%n",
                bill.getBillId(), bill.getBillDate(), bill.getTotalAmount(),
                bill.getPaymentMethod(), bill.getStatus());
    }
    private void cancelBill() throws Exception {
        int billId = readInt("Bill ID to cancel: ");
        System.out.print("Type YES to confirm cancellation: ");
        if (!"YES".equalsIgnoreCase(scanner.nextLine().trim())) {
            System.out.println("Cancellation aborted.");
            return;
        }
        billingService.cancelBill(billId, loggedInUser.getUserId());
        System.out.println("Bill cancelled and stock restored.");
    }
    private int readInt(String prompt) { System.out.print(prompt); return Integer.parseInt(scanner.nextLine().trim()); }
    private BigDecimal readAmount(String prompt) { System.out.print(prompt); return new BigDecimal(scanner.nextLine().trim()); }
}
