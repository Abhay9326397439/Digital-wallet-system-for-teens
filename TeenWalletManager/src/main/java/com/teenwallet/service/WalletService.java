package com.teenwallet.service;

import com.teenwallet.model.*;
import com.teenwallet.dao.TransactionDAO;
import com.teenwallet.dao.SettingsDAO;
import com.teenwallet.dao.SavingsGoalDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class WalletService {
    private static TransactionDAO transactionDAO = new TransactionDAO();
    private static SettingsDAO settingsDAO = new SettingsDAO();
    private static SavingsGoalDAO savingsGoalDAO = new SavingsGoalDAO();

    public static double getBalance() {
        return transactionDAO.getCurrentBalance();
    }

    public static boolean addMoney(double amount, String note) {
        if (amount <= 0) return false;

        double currentBalance = getBalance();
        double newBalance = currentBalance + amount;

        Transaction transaction = new Transaction(
                0, LocalDateTime.now(), Transaction.TransactionType.CREDIT,
                amount, "Parent", note, newBalance
        );

        return transactionDAO.addTransaction(transaction);
    }

    public static PaymentResult makePayment(double amount, String category) {
        double currentBalance = getBalance();
        UserSettings settings = settingsDAO.getSettings();

        // Check card lock
        if (settings.isCardLocked()) {
            return new PaymentResult(false, "Card is locked by parent!");
        }

        // Check sufficient balance
        if (currentBalance < amount) {
            return new PaymentResult(false, "Insufficient balance!");
        }

        // Check daily limit
        double dailySpent = getDailySpent();
        if (dailySpent + amount > settings.getDailyLimit()) {
            double remaining = settings.getDailyLimit() - dailySpent;
            return new PaymentResult(false,
                    String.format("Daily limit exceeded! ₹%.2f left today", remaining));
        }

        // Check weekly limit
        double weeklySpent = getWeeklySpent();
        if (weeklySpent + amount > settings.getWeeklyLimit()) {
            double remaining = settings.getWeeklyLimit() - weeklySpent;
            return new PaymentResult(false,
                    String.format("Weekly limit exceeded! ₹%.2f left this week", remaining));
        }

        // Check category limit - FIXED: categorySpent is now properly declared
        Double categoryLimit = settings.getCategoryLimits().get(category);
        if (categoryLimit != null) {
            double categorySpent = getCategorySpent(category); // FIXED: Added declaration
            if (categorySpent + amount > categoryLimit) {
                double remaining = categoryLimit - categorySpent;
                return new PaymentResult(false,
                        String.format("%s limit exceeded! ₹%.2f left", category, remaining));
            }
        }

        // All checks passed, process payment
        double newBalance = currentBalance - amount;
        Transaction transaction = new Transaction(
                0, LocalDateTime.now(), Transaction.TransactionType.DEBIT,
                amount, category, "Payment for " + category, newBalance
        );

        boolean success = transactionDAO.addTransaction(transaction);

        if (success) {
            // Check if any limit is near 80%
            checkLimitsWarning(dailySpent + amount, weeklySpent + amount,
                    getCategorySpent(category) + amount, category, settings);
        }

        return new PaymentResult(success, success ? "Payment successful!" : "Payment failed!");
    }

    public static boolean transferToGoal(int goalId, double amount) {
        double currentBalance = getBalance();

        if (currentBalance < amount) {
            return false;
        }

        // Update goal
        SavingsGoal goal = savingsGoalDAO.getGoalById(goalId);
        if (goal == null) {
            return false;
        }

        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        boolean goalUpdated = savingsGoalDAO.updateGoal(goal);

        if (goalUpdated) {
            // Create transaction
            double newBalance = currentBalance - amount;
            Transaction transaction = new Transaction(
                    0, LocalDateTime.now(), Transaction.TransactionType.SAVINGS,
                    amount, "Savings", "Transfer to goal: " + goal.getName(), newBalance
            );

            transactionDAO.addTransaction(transaction);

            // Check goal progress
            checkGoalProgress(goal);

            return true;
        }

        return false;
    }

    public static boolean parentBonusToGoal(int goalId, double amount) {
        SavingsGoal goal = savingsGoalDAO.getGoalById(goalId);
        if (goal == null) return false;

        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        boolean goalUpdated = savingsGoalDAO.updateGoal(goal);

        if (goalUpdated) {
            Transaction transaction = new Transaction(
                    0, LocalDateTime.now(), Transaction.TransactionType.CREDIT,
                    amount, "Parent Bonus", "Bonus for goal: " + goal.getName(),
                    getBalance() + amount
            );
            transactionDAO.addTransaction(transaction);

            checkGoalProgress(goal);
            return true;
        }

        return false;
    }

    private static double getDailySpent() {
        return transactionDAO.getDailySpent();
    }

    private static double getWeeklySpent() {
        return transactionDAO.getWeeklySpent();
    }

    private static double getCategorySpent(String category) {
        LocalDate now = LocalDate.now();
        return transactionDAO.getCategorySpent(category, now.getMonthValue(), now.getYear());
    }

    private static void checkLimitsWarning(double daily, double weekly,
                                           double category, String cat, UserSettings settings) {
        double dailyPercent = (daily / settings.getDailyLimit()) * 100;
        double weeklyPercent = (weekly / settings.getWeeklyLimit()) * 100;
        Double catLimit = settings.getCategoryLimits().get(cat);
        double categoryPercent = catLimit != null ? (category / catLimit) * 100 : 0;

        if (dailyPercent >= 80) {
            System.out.println("WARNING: Daily limit at " + String.format("%.1f", dailyPercent) + "%");
        }
        if (weeklyPercent >= 80) {
            System.out.println("WARNING: Weekly limit at " + String.format("%.1f", weeklyPercent) + "%");
        }
        if (categoryPercent >= 80) {
            System.out.println("WARNING: Category limit for " + cat + " at " +
                    String.format("%.1f", categoryPercent) + "%");
        }
    }

    private static void checkGoalProgress(SavingsGoal goal) {
        double percent = goal.getProgressPercentage();
        if (percent >= 100) {
            System.out.println("GOAL COMPLETED: " + goal.getName() + "!");
        } else if (percent >= 80) {
            System.out.println("GOAL ALERT: " + goal.getName() +
                    " is " + String.format("%.1f", percent) + "% complete!");
        }
    }

    public static class PaymentResult {
        private final boolean success;
        private final String message;

        public PaymentResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}