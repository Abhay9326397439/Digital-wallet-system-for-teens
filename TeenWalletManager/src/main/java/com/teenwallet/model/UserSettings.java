package com.teenwallet.model;

import java.util.HashMap;
import java.util.Map;

public class UserSettings {
    private double dailyLimit;
    private double weeklyLimit;
    private boolean cardLocked;
    private Map<String, Double> categoryLimits;

    public UserSettings() {
        this.dailyLimit = 300.0;
        this.weeklyLimit = 1500.0;
        this.cardLocked = false;
        this.categoryLimits = new HashMap<>();

        // Default category limits
        categoryLimits.put("Food", 100.0);
        categoryLimits.put("Entertainment", 150.0);
        categoryLimits.put("Education", 200.0);
        categoryLimits.put("Shopping", 200.0);
        categoryLimits.put("Transport", 100.0);
        categoryLimits.put("Others", 100.0);
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(dailyLimit).append("|").append(weeklyLimit).append("|").append(cardLocked);
        for (Map.Entry<String, Double> entry : categoryLimits.entrySet()) {
            sb.append("|").append(entry.getKey()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    public static UserSettings fromFileString(String line) {
        UserSettings settings = new UserSettings();
        String[] parts = line.split("\\|");
        settings.dailyLimit = Double.parseDouble(parts[0]);
        settings.weeklyLimit = Double.parseDouble(parts[1]);
        settings.cardLocked = Boolean.parseBoolean(parts[2]);

        settings.categoryLimits.clear();
        for (int i = 3; i < parts.length; i++) {
            String[] limitParts = parts[i].split(":");
            settings.categoryLimits.put(limitParts[0], Double.parseDouble(limitParts[1]));
        }
        return settings;
    }

    // Getters and setters
    public double getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(double dailyLimit) { this.dailyLimit = dailyLimit; }

    public double getWeeklyLimit() { return weeklyLimit; }
    public void setWeeklyLimit(double weeklyLimit) { this.weeklyLimit = weeklyLimit; }

    public boolean isCardLocked() { return cardLocked; }
    public void setCardLocked(boolean cardLocked) { this.cardLocked = cardLocked; }

    public Map<String, Double> getCategoryLimits() { return categoryLimits; }
    public void setCategoryLimits(Map<String, Double> categoryLimits) { this.categoryLimits = categoryLimits; }

    public void setCategoryLimit(String category, double limit) {
        categoryLimits.put(category, limit);
    }
}