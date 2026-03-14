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

    public SavingsGoal(int id, String name, double targetAmount, double currentAmount,
                       LocalDate targetDate, boolean completed) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.completed = completed;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getTargetDate() { return targetDate; }
    public boolean isCompleted() { return completed; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
        if (currentAmount >= targetAmount) {
            this.completed = true;
        }
    }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return String.format("%d|%s|%.2f|%.2f|%s|%b",
                id, name, targetAmount, currentAmount, targetDate.format(formatter), completed);
    }

    public static SavingsGoal fromFileString(String line) {
        String[] parts = line.split("\\|");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return new SavingsGoal(
                Integer.parseInt(parts[0]),
                parts[1],
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                LocalDate.parse(parts[4], formatter),
                Boolean.parseBoolean(parts[5])
        );
    }

    public double getProgressPercentage() {
        return (currentAmount / targetAmount) * 100;
    }

    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }

    public String getProgressColor() {
        double percentage = getProgressPercentage();
        if (percentage >= 100) return "#4CAF50";
        if (percentage >= 80) return "#2196F3";
        if (percentage >= 50) return "#FF9800";
        return "#F44336";
    }
}