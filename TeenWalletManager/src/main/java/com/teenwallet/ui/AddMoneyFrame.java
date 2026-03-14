package com.teenwallet.ui;

import com.teenwallet.service.WalletService;

import javax.swing.*;
import java.awt.*;

public class AddMoneyFrame extends JFrame {
    private JTextField amountField;
    private JTextField noteField;
    private JLabel messageLabel;

    public AddMoneyFrame() {
        setTitle("Add Money to Teen Wallet");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Add Money to Teen's Wallet");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Amount label
        JLabel amountLabel = new JLabel("Amount (₹):");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(amountLabel, gbc);

        // Amount field
        amountField = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(amountField, gbc);

        // Note label
        JLabel noteLabel = new JLabel("Note:");
        noteLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(noteLabel, gbc);

        // Note field
        noteField = new JTextField(15);
        noteField.setText("Allowance");
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(noteField, gbc);

        // Current balance info
        double currentBalance = WalletService.getBalance();
        JLabel balanceLabel = new JLabel("Current balance: ₹" + String.format("%.2f", currentBalance));
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(balanceLabel, gbc);

        // Add button
        JButton addButton = new JButton("Add Money");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        gbc.gridy = 4;
        mainPanel.add(addButton, gbc);

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 5;
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        // Action listeners
        addButton.addActionListener(e -> addMoney());
        amountField.addActionListener(e -> addMoney());
    }

    private void addMoney() {
        String amountText = amountField.getText().trim();

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

            String note = noteField.getText().trim();
            if (note.isEmpty()) {
                note = "Added by parent";
            }

            boolean success = WalletService.addMoney(amount, note);

            if (success) {
                showMessage("Money added successfully!", false);
                amountField.setText("");

                // Close after 1 second
                Timer timer = new Timer(1000, e -> dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                showMessage("Failed to add money", true);
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