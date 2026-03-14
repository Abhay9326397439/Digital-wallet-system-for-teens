package com.teenwallet.utils;

import com.teenwallet.model.Transaction;
import com.teenwallet.model.SavingsGoal;
import com.teenwallet.model.UserSettings;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class FileManager {
    private static final String DATA_DIR = "src/main/resources/data/";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "transactions.txt";
    private static final String GOALS_FILE = DATA_DIR + "goals.txt";
    private static final String SETTINGS_FILE = DATA_DIR + "settings.txt";

    public static void initializeDataFiles() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));

            // Initialize transactions with sample data if file doesn't exist
            File transactionsFile = new File(TRANSACTIONS_FILE);
            if (!transactionsFile.exists()) {
                createSampleTransactions();
            }

            // Initialize goals with sample data if file doesn't exist
            File goalsFile = new File(GOALS_FILE);
            if (!goalsFile.exists()) {
                createSampleGoals();
            }

            // Initialize settings with default values if file doesn't exist
            File settingsFile = new File(SETTINGS_FILE);
            if (!settingsFile.exists()) {
                saveSettings(new UserSettings());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createSampleTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        transactions.add(new Transaction(1, now.minusDays(10),
                Transaction.TransactionType.CREDIT, 1000.0, "Parent",
                "Initial balance", 1000.0));
        transactions.add(new Transaction(2, now.minusDays(8),
                Transaction.TransactionType.DEBIT, 50.0, "Food",
                "Lunch", 950.0));
        transactions.add(new Transaction(3, now.minusDays(5),
                Transaction.TransactionType.DEBIT, 30.0, "Transport",
                "Bus fare", 920.0));
        transactions.add(new Transaction(4, now.minusDays(3),
                Transaction.TransactionType.CREDIT, 200.0, "Parent",
                "Allowance", 1120.0));
        transactions.add(new Transaction(5, now.minusDays(1),
                Transaction.TransactionType.DEBIT, 45.0, "Entertainment",
                "Movie ticket", 1075.0));

        saveTransactions(transactions);
    }

    private static void createSampleGoals() {
        List<SavingsGoal> goals = new ArrayList<>();
        LocalDate now = LocalDate.now();

        goals.add(new SavingsGoal(1, "New Gaming Laptop", 50000.0, 12500.0,
                now.plusMonths(6), false));
        goals.add(new SavingsGoal(2, "Smart Watch", 15000.0, 12000.0,
                now.plusMonths(2), false));
        goals.add(new SavingsGoal(3, "Concert Tickets", 5000.0, 5000.0,
                now.plusMonths(1), true));

        saveGoals(goals);
    }

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    transactions.add(Transaction.fromFileString(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : transactions) {
                writer.write(t.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addTransaction(Transaction transaction) {
        List<Transaction> transactions = loadTransactions();
        transaction = new Transaction(
                transactions.size() + 1,
                transaction.getDate(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getBalanceAfter()
        );
        transactions.add(transaction);
        saveTransactions(transactions);
    }

    public static List<SavingsGoal> loadGoals() {
        List<SavingsGoal> goals = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(GOALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    goals.add(SavingsGoal.fromFileString(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goals;
    }

    public static void saveGoals(List<SavingsGoal> goals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GOALS_FILE))) {
            for (SavingsGoal g : goals) {
                writer.write(g.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addGoal(SavingsGoal goal) {
        List<SavingsGoal> goals = loadGoals();
        goal = new SavingsGoal(
                goals.size() + 1,
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getTargetDate(),
                goal.isCompleted()
        );
        goals.add(goal);
        saveGoals(goals);
    }

    public static void updateGoal(SavingsGoal updatedGoal) {
        List<SavingsGoal> goals = loadGoals();
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getId() == updatedGoal.getId()) {
                goals.set(i, updatedGoal);
                break;
            }
        }
        saveGoals(goals);
    }

    public static UserSettings loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            return new UserSettings();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return UserSettings.fromFileString(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UserSettings();
    }

    public static void saveSettings(UserSettings settings) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            writer.write(settings.toFileString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double getCurrentBalance() {
        List<Transaction> transactions = loadTransactions();
        if (transactions.isEmpty()) {
            return 0.0;
        }
        return transactions.get(transactions.size() - 1).getBalanceAfter();
    }
}