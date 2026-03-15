package com.teenwallet.service;

import com.teenwallet.model.*;
import com.teenwallet.dao.TransactionDAO;
import com.teenwallet.dao.SettingsDAO;
import com.teenwallet.dao.SavingsGoalDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WalletService {

    public static double getBalanceForUser(String username) {
        return TransactionDAO.getCurrentBalanceForUser(username);
    }

    public static double getDailySpentForUser(String username) {
        return TransactionDAO.getDailySpentForUser(username);
    }

    public static double getWeeklySpentForUser(String username) {
        return TransactionDAO.getWeeklySpentForUser(username);
    }

    public static double getCategorySpentForUser(String username, String category) {
        LocalDate now = LocalDate.now();
        return TransactionDAO.getCategorySpentForUser(username, category, now.getMonthValue(), now.getYear());
    }

    public static boolean addMoneyForUser(String username, double amount, String note) {
        if (amount <= 0) return false;

        double currentBalance = getBalanceForUser(username);
        double newBalance = currentBalance + amount;

        Transaction transaction = new Transaction(
                0, LocalDateTime.now(), Transaction.TransactionType.CREDIT,
                amount, "Parent", note, newBalance, username
        );

        return TransactionDAO.addTransaction(transaction);
    }

    public static PaymentResult makePaymentForUser(String username, double amount, String category) {
        double currentBalance = getBalanceForUser(username);
        UserSettings settings = SettingsDAO.getSettingsForUser(username);

        if (settings.isCardLocked()) {
            return new PaymentResult(false, "❌ Card is locked by parent!");
        }

        if (currentBalance < amount) {
            return new PaymentResult(false, "❌ Insufficient balance! You have ₹" +
                    String.format("%.2f", currentBalance));
        }

        double dailySpent = getDailySpentForUser(username);
        if (dailySpent + amount > settings.getDailyLimit()) {
            double remaining = settings.getDailyLimit() - dailySpent;
            return new PaymentResult(false,
                    String.format("❌ Daily limit exceeded! ₹%.2f left today", Math.max(0, remaining)));
        }

        double weeklySpent = getWeeklySpentForUser(username);
        if (weeklySpent + amount > settings.getWeeklyLimit()) {
            double remaining = settings.getWeeklyLimit() - weeklySpent;
            return new PaymentResult(false,
                    String.format("❌ Weekly limit exceeded! ₹%.2f left this week", Math.max(0, remaining)));
        }

        Double categoryLimit = settings.getCategoryLimits().get(category);
        if (categoryLimit != null) {
            double categorySpent = getCategorySpentForUser(username, category);
            if (categorySpent + amount > categoryLimit) {
                double remaining = categoryLimit - categorySpent;
                return new PaymentResult(false,
                        String.format("❌ %s limit exceeded! ₹%.2f left", category, Math.max(0, remaining)));
            }
        }

        double newBalance = currentBalance - amount;
        Transaction transaction = new Transaction(
                0, LocalDateTime.now(), Transaction.TransactionType.DEBIT,
                amount, category, "Payment for " + category, newBalance, username
        );

        boolean success = TransactionDAO.addTransaction(transaction);

        return new PaymentResult(success, success ?
                "✅ Payment successful! ₹" + String.format("%.2f", amount) + " deducted" :
                "❌ Payment failed!");
    }

    public static boolean transferToGoal(int goalId, double amount, String username) {
        double currentBalance = getBalanceForUser(username);

        if (currentBalance < amount) {
            return false;
        }

        SavingsGoalDAO goalDAO = new SavingsGoalDAO();
        SavingsGoal goal = goalDAO.getGoalById(goalId);
        if (goal == null) return false;

        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        boolean goalUpdated = goalDAO.updateGoal(goal);

        if (goalUpdated) {
            double newBalance = currentBalance - amount;
            Transaction transaction = new Transaction(
                    0, LocalDateTime.now(), Transaction.TransactionType.SAVINGS,
                    amount, "Savings", "Transfer to goal: " + goal.getName(), newBalance, username
            );

            TransactionDAO.addTransaction(transaction);
            return true;
        }

        return false;
    }

    public static boolean parentBonusToGoal(int goalId, double amount, String username) {
        SavingsGoalDAO goalDAO = new SavingsGoalDAO();
        SavingsGoal goal = goalDAO.getGoalById(goalId);
        if (goal == null) return false;

        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        boolean goalUpdated = goalDAO.updateGoal(goal);

        if (goalUpdated) {
            double currentBalance = getBalanceForUser(username);
            Transaction transaction = new Transaction(
                    0, LocalDateTime.now(), Transaction.TransactionType.CREDIT,
                    amount, "Parent Bonus", "Bonus for goal: " + goal.getName(),
                    currentBalance + amount, username
            );
            TransactionDAO.addTransaction(transaction);
            return true;
        }

        return false;
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