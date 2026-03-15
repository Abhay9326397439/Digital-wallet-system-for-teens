package com.teenwallet.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private int id;
    private LocalDateTime date;
    private TransactionType type;
    private double amount;
    private String category;
    private String description;
    private double balanceAfter;
    private String username;

    public enum TransactionType {
        CREDIT, DEBIT, SAVINGS
    }

    public Transaction(int id, LocalDateTime date, TransactionType type,
                       double amount, String category, String description, double balanceAfter) {
        this(id, date, type, amount, category, description, balanceAfter, "teen1");
    }

    public Transaction(int id, LocalDateTime date, TransactionType type,
                       double amount, String category, String description, double balanceAfter, String username) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.username = username;
    }

    // Getters
    public int getId() { return id; }
    public LocalDateTime getDate() { return date; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getUsername() { return username; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }
    public void setUsername(String username) { this.username = username; }

    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format("%d|%s|%s|%.2f|%s|%s|%.2f|%s",
                id, date.format(formatter), type, amount, category, description, balanceAfter, username);
    }

    public static Transaction fromFileString(String line) {
        String[] parts = line.split("\\|");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return new Transaction(
                Integer.parseInt(parts[0]),
                LocalDateTime.parse(parts[1], formatter),
                TransactionType.valueOf(parts[2]),
                Double.parseDouble(parts[3]),
                parts[4],
                parts[5],
                Double.parseDouble(parts[6]),
                parts.length > 7 ? parts[7] : "teen1"
        );
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

    public String getFormattedAmount() {
        return String.format("₹%.2f", amount);
    }

    public String getFormattedBalance() {
        return String.format("₹%.2f", balanceAfter);
    }

    public String getColorCode() {
        return type == TransactionType.CREDIT ? "#4CAF50" :
                type == TransactionType.DEBIT ? "#F44336" : "#FF9800";
    }
} // <-- This closing brace was missing