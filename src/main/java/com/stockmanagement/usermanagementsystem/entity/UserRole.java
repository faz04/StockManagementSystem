package com.stockmanagement.usermanagementsystem.entity;

public enum UserRole {
    ADMIN("Admin"),
    STOCK_MANAGER("Stock Manager"),
    SALES_STAFF("Sales Staff"),
    HR_STAFF("HR Staff"),
    MARKETING_MANAGER("Marketing Manager");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}