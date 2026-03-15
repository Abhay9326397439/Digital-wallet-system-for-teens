package com.teenwallet.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class SavingsGoal {
    private int id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private LocalDate targetDate;
    private boolean completed;
    private String username;

    public SavingsGoal(int id, String name, double targetAmount, double currentAmount,
                       LocalDate targetDate, boolean completed, String username) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.completed = completed;
        this.username = username;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
        if (currentAmount >= targetAmount) {
            this.completed = true;
        }
    }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getProgressPercentage() {
        return (currentAmount / targetAmount) * 100;
    }

    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }
}