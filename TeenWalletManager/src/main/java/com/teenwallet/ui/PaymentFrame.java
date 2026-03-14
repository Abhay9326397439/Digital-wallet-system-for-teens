package com.teenwallet.ui;

import com.teenwallet.service.WalletService;
import com.teenwallet.utils.FileManager;
import com.teenwallet.model.UserSettings;

import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JLabel messageLabel;
    private JLabel balanceLabel;
    private JLabel dailyLimitLabel;
    private JLabel weeklyLimitLabel;

    private static final String[] CATEGORIES = {
            "Food", "Entertainment", "Education", "Shopping", "Transport", "Others"
    };

    public PaymentFrame() {
        setTitle("Make Payment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
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

        // Title
        JLabel titleLabel = new JLabel("Make a Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Current balance
        double balance = WalletService.getBalance();
        balanceLabel = new JLabel("Current Balance: ₹" + String.format("%.2f", balance));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 1;
        mainPanel.add(balanceLabel, gbc);

        // Limit info panel
        JPanel limitPanel = createLimitPanel();
        gbc.gridy = 2;
        mainPanel.add(limitPanel, gbc);

        // Category label
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(categoryLabel, gbc);

        // Category combo
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(categoryCombo, gbc);

        // Amount label
        JLabel amountLabel = new JLabel("Amount (₹):");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(amountLabel, gbc);

        // Amount field
        amountField = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(amountField, gbc);

        // Pay button
        JButton payButton = new JButton("Pay Now");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payButton.setBackground(new Color(70, 130, 180));
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);
        payButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(payButton, gbc);

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 6;
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        // Action listeners
        payButton.addActionListener(e -> processPayment());
        amountField.addActionListener(e -> processPayment());
        categoryCombo.addActionListener(e -> updateCategoryLimit());
    }

    private JPanel createLimitPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        UserSettings settings = FileManager.loadSettings();

        panel.add(new JLabel("Daily Limit:"));
        dailyLimitLabel = new JLabel("₹" + String.format("%.2f", settings.getDailyLimit()));
        dailyLimitLabel.setForeground(new Color(70, 130, 180));
        panel.add(dailyLimitLabel);

        panel.add(new JLabel("Weekly Limit:"));
        weeklyLimitLabel = new JLabel("₹" + String.format("%.2f", settings.getWeeklyLimit()));
        weeklyLimitLabel.setForeground(new Color(70, 130, 180));
        panel.add(weeklyLimitLabel);

        return panel;
    }

    private void updateLimitInfo() {
        UserSettings settings = FileManager.loadSettings();
        dailyLimitLabel.setText("₹" + String.format("%.2f", settings.getDailyLimit()));
        weeklyLimitLabel.setText("₹" + String.format("%.2f", settings.getWeeklyLimit()));
    }

    private void updateCategoryLimit() {
        String category = (String) categoryCombo.getSelectedItem();
        UserSettings settings = FileManager.loadSettings();
        Double limit = settings.getCategoryLimits().get(category);

        if (limit != null) {
            messageLabel.setText(category + " limit: ₹" + String.format("%.2f", limit));
            messageLabel.setForeground(new Color(70, 130, 180));
        }
    }

    private void processPayment() {
        String amountText = amountField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (amountText.isEmpty()) {
            showMessage("Please enter an amount", true);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                showMessage("Amount must be positive", true);
                return;
            }

            WalletService.PaymentResult result = WalletService.makePayment(amount, category);

            if (result.isSuccess()) {
                showMessage("✅ " + result.getMessage(), false);
                amountField.setText("");

                // Update balance display
                double newBalance = WalletService.getBalance();
                balanceLabel.setText("Current Balance: ₹" + String.format("%.2f", newBalance));

                // Close after 1.5 seconds
                Timer timer = new Timer(1500, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                showMessage("❌ " + result.getMessage(), true);
            }

        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid number", true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : new Color(0, 150, 0));
    }
}