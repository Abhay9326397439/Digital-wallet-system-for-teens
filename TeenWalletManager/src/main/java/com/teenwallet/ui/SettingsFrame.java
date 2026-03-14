package com.teenwallet.ui;

import com.teenwallet.dao.SettingsDAO;
import com.teenwallet.model.UserSettings;
import javax.swing.*;
import java.awt.*;

public class SettingsFrame extends JFrame {
    private SettingsDAO settingsDAO;
    private UserSettings settings;

    private JTextField dailyLimitField;
    private JTextField weeklyLimitField;
    private JCheckBox cardLockCheckBox;
    private JTextField foodLimitField;
    private JTextField entertainmentLimitField;
    private JTextField educationLimitField;
    private JTextField shoppingLimitField;
    private JTextField transportLimitField;
    private JTextField othersLimitField;
    private JLabel messageLabel;

    public SettingsFrame() {
        setTitle("Settings - Parent Controls");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        settingsDAO = new SettingsDAO();
        settings = settingsDAO.getSettings();

        initComponents();
        loadSettings();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Parent Control Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Limit Settings Section
        JLabel limitsLabel = new JLabel("Spending Limits");
        limitsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        limitsLabel.setForeground(new Color(25, 25, 112));
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(limitsLabel, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);

        // Daily Limit
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Daily Limit (₹):"), gbc);

        dailyLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(dailyLimitField, gbc);

        // Weekly Limit
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Weekly Limit (₹):"), gbc);

        weeklyLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(weeklyLimitField, gbc);

        // Card Lock
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Card Status:"), gbc);

        cardLockCheckBox = new JCheckBox("Lock Card");
        cardLockCheckBox.setBackground(new Color(240, 248, 255));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(cardLockCheckBox, gbc);

        // Category Limits Section
        JLabel categoryLabel = new JLabel("Category-wise Limits");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        categoryLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(categoryLabel, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;

        // Food Limit
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Food (₹):"), gbc);

        foodLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(foodLimitField, gbc);

        // Entertainment Limit
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Entertainment (₹):"), gbc);

        entertainmentLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(entertainmentLimitField, gbc);

        // Education Limit
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Education (₹):"), gbc);

        educationLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(educationLimitField, gbc);

        // Shopping Limit
        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Shopping (₹):"), gbc);

        shoppingLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(shoppingLimitField, gbc);

        // Transport Limit
        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Transport (₹):"), gbc);

        transportLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(transportLimitField, gbc);

        // Others Limit
        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Others (₹):"), gbc);

        othersLimitField = new JTextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(othersLimitField, gbc);

        // Save Button
        JButton saveButton = new JButton("Save Settings");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(saveButton, gbc);

        // Message Label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 13;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        saveButton.addActionListener(e -> saveSettings());
    }

    private void loadSettings() {
        dailyLimitField.setText(String.valueOf(settings.getDailyLimit()));
        weeklyLimitField.setText(String.valueOf(settings.getWeeklyLimit()));
        cardLockCheckBox.setSelected(settings.isCardLocked());

        // Load category limits
        foodLimitField.setText(String.valueOf(settingsDAO.getCategoryLimit("Food")));
        entertainmentLimitField.setText(String.valueOf(settingsDAO.getCategoryLimit("Entertainment")));
        educationLimitField.setText(String.valueOf(settingsDAO.getCategoryLimit("Education")));
        shoppingLimitField.setText(String.valueOf(settingsDAO.getCategoryLimit("Shopping")));
        transportLimitField.setText(String.valueOf(settingsDAO.getCategoryLimit("Transport")));
        othersLimitField.setText(String.valueOf(settingsDAO.getCategoryLimit("Others")));
    }

    private void saveSettings() {
        try {
            // Validate and save main settings
            double dailyLimit = Double.parseDouble(dailyLimitField.getText().trim());
            double weeklyLimit = Double.parseDouble(weeklyLimitField.getText().trim());

            if (dailyLimit <= 0 || weeklyLimit <= 0) {
                showMessage("Limits must be positive numbers", true);
                return;
            }

            settings.setDailyLimit(dailyLimit);
            settings.setWeeklyLimit(weeklyLimit);
            settings.setCardLocked(cardLockCheckBox.isSelected());

            boolean success = settingsDAO.saveSettings(settings);

            // Save category limits
            success &= settingsDAO.updateCategoryLimit("Food", Double.parseDouble(foodLimitField.getText().trim()));
            success &= settingsDAO.updateCategoryLimit("Entertainment", Double.parseDouble(entertainmentLimitField.getText().trim()));
            success &= settingsDAO.updateCategoryLimit("Education", Double.parseDouble(educationLimitField.getText().trim()));
            success &= settingsDAO.updateCategoryLimit("Shopping", Double.parseDouble(shoppingLimitField.getText().trim()));
            success &= settingsDAO.updateCategoryLimit("Transport", Double.parseDouble(transportLimitField.getText().trim()));
            success &= settingsDAO.updateCategoryLimit("Others", Double.parseDouble(othersLimitField.getText().trim()));

            if (success) {
                showMessage("Settings saved successfully!", false);

                // Close after 1 second
                Timer timer = new Timer(1000, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                showMessage("Failed to save settings", true);
            }

        } catch (NumberFormatException ex) {
            showMessage("Please enter valid numbers for all limits", true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : new Color(0, 150, 0));
    }
}