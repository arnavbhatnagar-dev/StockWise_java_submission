package com.stockwise.enums;

public enum Role {
    ADMIN,
    CASHIER,
    INVENTORY_MANAGER;

    public boolean canUseInventory() {
        return this == ADMIN || this == INVENTORY_MANAGER;
    }

    public boolean canUseBilling() {
        return this == ADMIN || this == CASHIER;
    }

    public boolean canUsePurchases() {
        return this == ADMIN || this == INVENTORY_MANAGER;
    }

    public boolean canUseReports() {
        return this == ADMIN || this == INVENTORY_MANAGER;
    }
}
