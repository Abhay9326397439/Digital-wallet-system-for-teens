package com.teenwallet.model;

import java.util.HashMap;
import java.util.Map;

public class UserSettings {
    private String username;
    private double dailyLimit;
    private double weeklyLimit;
    private boolean cardLocked;
    private Map<String, Double> categoryLimits;

    public UserSettings(String username) {
        this.username = username;
        this.dailyLimit = 300.0;
        this.weeklyLimit = 1500.0;
        this.cardLocked = false;
        this.categoryLimits = new HashMap<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

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