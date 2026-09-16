package com.stockwise.model;

public class Supplier {
    private final int supplierId;
    private final String supplierName;
    private final String phone;
    private final String email;
    private final String address;

    public Supplier(String supplierName, String phone, String email, String address) {
        this(0, supplierName, phone, email, address);
    }
    public Supplier(int supplierId, String supplierName, String phone, String email, String address) {
        this.supplierId = supplierId; this.supplierName = supplierName; this.phone = phone;
        this.email = email; this.address = address;
    }
    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
}
