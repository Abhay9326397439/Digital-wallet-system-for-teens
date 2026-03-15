package com.teenwallet.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String parentUsername;
    private boolean active;
    private double walletBalance;

    public User(int id, String username, String password, String role, String parentUsername, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.parentUsername = parentUsername;
        this.active = active;
        this.walletBalance = 0.0;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getParentUsername() { return parentUsername; }
    public void setParentUsername(String parentUsername) { this.parentUsername = parentUsername; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    public boolean isParent() {
        return "PARENT".equals(role);
    }

    public boolean isTeen() {
        return "TEEN".equals(role);
    }
}