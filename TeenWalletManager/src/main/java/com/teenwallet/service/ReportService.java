package com.teenwallet.service;

import com.teenwallet.dao.TransactionDAO;
import com.teenwallet.dao.SavingsGoalDAO;
import com.teenwallet.model.Transaction;
import com.teenwallet.model.SavingsGoal;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private TransactionDAO transactionDAO;
    private SavingsGoalDAO savingsGoalDAO;

    public ReportService() {
        this.transactionDAO = new TransactionDAO();
        this.savingsGoalDAO = new SavingsGoalDAO();
    }

    public MonthlyReport generateMonthlyReport(int month, int year) {
        MonthlyReport report = new MonthlyReport();
        report.setMonth(month);
        report.setYear(year);

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<Transaction> monthlyTransactions = TransactionDAO.getTransactionsByDateRange(startDate, endDate);
        report.setTransactions(monthlyTransactions);

        double totalSpending = monthlyTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                .mapToDouble(Transaction::getAmount)
                .sum();
        report.setTotalSpending(totalSpending);

        Map<String, Double> categorySpending = monthlyTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        report.setCategorySpending(categorySpending);

        List<SavingsGoal> allGoals = savingsGoalDAO.getAllGoals();
        report.setSavingsGoals(allGoals);

        double totalSaved = allGoals.stream()
                .mapToDouble(SavingsGoal::getCurrentAmount)
                .sum();
        report.setTotalSaved(totalSaved);

        int completedGoals = (int) allGoals.stream()
                .filter(SavingsGoal::isCompleted)
                .count();
        report.setCompletedGoals(completedGoals);

        return report;
    }

    public MonthlyReport generateMonthlyReportForUser(String username, int month, int year) {
        MonthlyReport report = new MonthlyReport();
        report.setMonth(month);
        report.setYear(year);

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<Transaction> monthlyTransactions = TransactionDAO.getTransactionsForUser(username).stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
        report.setTransactions(monthlyTransactions);

        double totalSpending = monthlyTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                .mapToDouble(Transaction::getAmount)
                .sum();
        report.setTotalSpending(totalSpending);

        Map<String, Double> categorySpending = monthlyTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        report.setCategorySpending(categorySpending);

        List<SavingsGoal> userGoals = savingsGoalDAO.getGoalsForUser(username);
        report.setSavingsGoals(userGoals);

        double totalSaved = userGoals.stream()
                .mapToDouble(SavingsGoal::getCurrentAmount)
                .sum();
        report.setTotalSaved(totalSaved);

        int completedGoals = (int) userGoals.stream()
                .filter(SavingsGoal::isCompleted)
                .count();
        report.setCompletedGoals(completedGoals);

        return report;
    }

    public static class MonthlyReport {
        private int month;
        private int year;
        private List<Transaction> transactions;
        private double totalSpending;
        private Map<String, Double> categorySpending;
        private List<SavingsGoal> savingsGoals;
        private double totalSaved;
        private int completedGoals;

        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

        public List<Transaction> getTransactions() { return transactions; }
        public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

        public double getTotalSpending() { return totalSpending; }
        public void setTotalSpending(double totalSpending) { this.totalSpending = totalSpending; }

        public Map<String, Double> getCategorySpending() { return categorySpending; }
        public void setCategorySpending(Map<String, Double> categorySpending) { this.categorySpending = categorySpending; }

        public List<SavingsGoal> getSavingsGoals() { return savingsGoals; }
        public void setSavingsGoals(List<SavingsGoal> savingsGoals) { this.savingsGoals = savingsGoals; }

        public double getTotalSaved() { return totalSaved; }
        public void setTotalSaved(double totalSaved) { this.totalSaved = totalSaved; }

        public int getCompletedGoals() { return completedGoals; }
        public void setCompletedGoals(int completedGoals) { this.completedGoals = completedGoals; }
    }
}