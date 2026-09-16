package com.stockwise.model;

import com.stockwise.enums.Role;

public class User {
    private final int userId;
    private final String username;
    private final String fullName;
    private final Role role;

    public User(int userId, String username, String fullName, Role role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }
}
