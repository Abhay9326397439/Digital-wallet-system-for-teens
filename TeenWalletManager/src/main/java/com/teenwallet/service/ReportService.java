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

        List<Transaction> monthlyTransactions = transactionDAO.getTransactionsByDateRange(startDate, endDate);
        report.setTransactions(monthlyTransactions);

        // Calculate total spending
        double totalSpending = monthlyTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                .mapToDouble(Transaction::getAmount)
                .sum();
        report.setTotalSpending(totalSpending);

        // Calculate category-wise spending
        Map<String, Double> categorySpending = monthlyTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        report.setCategorySpending(categorySpending);

        // Calculate savings progress
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

    public YearlyReport generateYearlyReport(int year) {
        YearlyReport report = new YearlyReport();
        report.setYear(year);

        Map<Integer, Double> monthlySpending = new HashMap<>();
        Map<String, Double> yearlyCategorySpending = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            MonthlyReport monthlyReport = generateMonthlyReport(month, year);
            monthlySpending.put(month, monthlyReport.getTotalSpending());

            monthlyReport.getCategorySpending().forEach((category, amount) -> {
                yearlyCategorySpending.merge(category, amount, Double::sum);
            });
        }

        report.setMonthlySpending(monthlySpending);
        report.setCategorySpending(yearlyCategorySpending);

        return report;
    }

    public List<Transaction> searchTransactions(String keyword, LocalDate startDate, LocalDate endDate) {
        List<Transaction> allTransactions = transactionDAO.getAllTransactions();

        return allTransactions.stream()
                .filter(t -> {
                    boolean matchesKeyword = keyword == null || keyword.isEmpty() ||
                            t.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                            t.getCategory().toLowerCase().contains(keyword.toLowerCase());

                    boolean matchesDateRange = true;
                    if (startDate != null) {
                        matchesDateRange = matchesDateRange && !t.getDate().toLocalDate().isBefore(startDate);
                    }
                    if (endDate != null) {
                        matchesDateRange = matchesDateRange && !t.getDate().toLocalDate().isAfter(endDate);
                    }

                    return matchesKeyword && matchesDateRange;
                })
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .collect(Collectors.toList());
    }

    public SpendingAnalysis getSpendingAnalysis() {
        SpendingAnalysis analysis = new SpendingAnalysis();

        // Current month spending
        LocalDate now = LocalDate.now();
        MonthlyReport currentMonth = generateMonthlyReport(now.getMonthValue(), now.getYear());
        analysis.setCurrentMonthSpending(currentMonth.getTotalSpending());

        // Previous month spending
        LocalDate previousMonth = now.minusMonths(1);
        MonthlyReport lastMonth = generateMonthlyReport(previousMonth.getMonthValue(), previousMonth.getYear());
        analysis.setPreviousMonthSpending(lastMonth.getTotalSpending());

        // Calculate trend
        if (lastMonth.getTotalSpending() > 0) {
            double trend = ((currentMonth.getTotalSpending() - lastMonth.getTotalSpending()) / lastMonth.getTotalSpending()) * 100;
            analysis.setSpendingTrend(trend);
        }

        // Top spending categories
        analysis.setTopCategories(currentMonth.getCategorySpending().entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList()));

        return analysis;
    }

    // Inner classes for report data
    public static class MonthlyReport {
        private int month;
        private int year;
        private List<Transaction> transactions;
        private double totalSpending;
        private Map<String, Double> categorySpending;
        private List<SavingsGoal> savingsGoals;
        private double totalSaved;
        private int completedGoals;

        // Getters and setters
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

    public static class YearlyReport {
        private int year;
        private Map<Integer, Double> monthlySpending;
        private Map<String, Double> categorySpending;

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

        public Map<Integer, Double> getMonthlySpending() { return monthlySpending; }
        public void setMonthlySpending(Map<Integer, Double> monthlySpending) { this.monthlySpending = monthlySpending; }

        public Map<String, Double> getCategorySpending() { return categorySpending; }
        public void setCategorySpending(Map<String, Double> categorySpending) { this.categorySpending = categorySpending; }
    }

    public static class SpendingAnalysis {
        private double currentMonthSpending;
        private double previousMonthSpending;
        private double spendingTrend;
        private List<Map.Entry<String, Double>> topCategories;

        public double getCurrentMonthSpending() { return currentMonthSpending; }
        public void setCurrentMonthSpending(double currentMonthSpending) { this.currentMonthSpending = currentMonthSpending; }

        public double getPreviousMonthSpending() { return previousMonthSpending; }
        public void setPreviousMonthSpending(double previousMonthSpending) { this.previousMonthSpending = previousMonthSpending; }

        public double getSpendingTrend() { return spendingTrend; }
        public void setSpendingTrend(double spendingTrend) { this.spendingTrend = spendingTrend; }

        public List<Map.Entry<String, Double>> getTopCategories() { return topCategories; }
        public void setTopCategories(List<Map.Entry<String, Double>> topCategories) { this.topCategories = topCategories; }

        public String getTrendDirection() {
            if (spendingTrend > 5) return "↑ Increasing";
            if (spendingTrend < -5) return "↓ Decreasing";
            return "→ Stable";
        }

        public Color getTrendColor() {
            if (spendingTrend > 5) return Color.RED;
            if (spendingTrend < -5) return Color.GREEN;
            return Color.ORANGE;
        }
    }
}