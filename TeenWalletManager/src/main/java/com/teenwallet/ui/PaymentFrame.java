package com.teenwallet.ui;

import com.teenwallet.service.WalletService;
import com.teenwallet.dao.SettingsDAO;
import com.teenwallet.model.UserSettings;

import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    private String teenUsername;
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JLabel messageLabel;
    private JLabel balanceLabel;
    private JLabel dailyLimitLabel;
    private JLabel weeklyLimitLabel;

    private static final String[] CATEGORIES = {
            "Food", "Entertainment", "Education", "Shopping", "Transport", "Others"
    };

    public PaymentFrame(String teenUsername) {
        this.teenUsername = teenUsername;
        setTitle("Make Payment - " + teenUsername);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        updateLimitInfo();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Make a Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        double balance = WalletService.getBalanceForUser(teenUsername);
        balanceLabel = new JLabel("Current Balance: ₹" + String.format("%.2f", balance));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(25, 25, 112));
        gbc.gridy = 1;
        mainPanel.add(balanceLabel, gbc);

        boolean isLocked = SettingsDAO.isCardLockedForUser(teenUsername);
        JLabel cardStatusLabel = new JLabel(isLocked ? "🔴 Card is LOCKED" : "🟢 Card is ACTIVE");
        cardStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardStatusLabel.setForeground(isLocked ? Color.RED : new Color(76, 175, 80));
        gbc.gridy = 2;
        mainPanel.add(cardStatusLabel, gbc);

        JPanel limitPanel = createLimitPanel();
        gbc.gridy = 3;
        mainPanel.add(limitPanel, gbc);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(categoryCombo, gbc);

        JLabel amountLabel = new JLabel("Amount (₹):");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(amountLabel, gbc);

        amountField = new JTextField(15);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(amountField, gbc);

        JButton payButton = new JButton("💳 Pay Now");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payButton.setBackground(new Color(144, 238, 144)); // Green
        payButton.setForeground(Color.BLACK); // White text
        payButton.setFocusPainted(false);
        payButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(payButton, gbc);

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        payButton.addActionListener(e -> processPayment());
        amountField.addActionListener(e -> processPayment());
        categoryCombo.addActionListener(e -> updateCategoryLimit());
    }

    private JPanel createLimitPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        UserSettings settings = SettingsDAO.getSettingsForUser(teenUsername);

        panel.add(new JLabel("Daily Limit:"));
        dailyLimitLabel = new JLabel("₹" + String.format("%.2f", settings.getDailyLimit()));
        dailyLimitLabel.setForeground(new Color(70, 130, 180));
        dailyLimitLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(dailyLimitLabel);

        panel.add(new JLabel("Weekly Limit:"));
        weeklyLimitLabel = new JLabel("₹" + String.format("%.2f", settings.getWeeklyLimit()));
        weeklyLimitLabel.setForeground(new Color(70, 130, 180));
        weeklyLimitLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(weeklyLimitLabel);

        double dailySpent = WalletService.getDailySpentForUser(teenUsername);
        double dailyRemaining = settings.getDailyLimit() - dailySpent;
        panel.add(new JLabel("Remaining Today:"));
        JLabel dailyRemainingLabel = new JLabel("₹" + String.format("%.2f", Math.max(0, dailyRemaining)));
        dailyRemainingLabel.setForeground(dailyRemaining > 0 ? new Color(76, 175, 80) : Color.RED);
        dailyRemainingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(dailyRemainingLabel);

        return panel;
    }

    private void updateLimitInfo() {
        UserSettings settings = SettingsDAO.getSettingsForUser(teenUsername);
        dailyLimitLabel.setText("₹" + String.format("%.2f", settings.getDailyLimit()));
        weeklyLimitLabel.setText("₹" + String.format("%.2f", settings.getWeeklyLimit()));
    }

    private void updateCategoryLimit() {
        String category = (String) categoryCombo.getSelectedItem();
        Double limit = SettingsDAO.getCategoryLimitForUser(teenUsername, category);

        if (limit != null) {
            double spent = WalletService.getCategorySpentForUser(teenUsername, category);
            double remaining = limit - spent;
            messageLabel.setText(category + " limit: ₹" + String.format("%.2f", limit) +
                    " (Spent: ₹" + String.format("%.2f", spent) +
                    ", Left: ₹" + String.format("%.2f", remaining) + ")");
            messageLabel.setForeground(remaining > 0 ? new Color(70, 130, 180) : Color.RED);
        }
    }

    private void processPayment() {
        String amountText = amountField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (amountText.isEmpty()) {
            showMessage("❌ Please enter an amount", true);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                showMessage("❌ Amount must be positive", true);
                return;
            }

            WalletService.PaymentResult result = WalletService.makePaymentForUser(teenUsername, amount, category);

            if (result.isSuccess()) {
                showMessage("✅ " + result.getMessage(), false);
                amountField.setText("");

                double newBalance = WalletService.getBalanceForUser(teenUsername);
                balanceLabel.setText("Current Balance: ₹" + String.format("%.2f", newBalance));

                Timer timer = new Timer(1500, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                showMessage("❌ " + result.getMessage(), true);
            }

        } catch (NumberFormatException ex) {
            showMessage("❌ Please enter a valid number", true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : new Color(0, 150, 0));
    }
}