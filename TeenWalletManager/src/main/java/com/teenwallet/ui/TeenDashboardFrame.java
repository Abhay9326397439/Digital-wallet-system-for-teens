package com.teenwallet.ui;

import com.teenwallet.model.User;
import com.teenwallet.service.AuthService;
import com.teenwallet.service.WalletService;
import com.teenwallet.ui.components.VirtualCardPanel;
import com.teenwallet.dao.SettingsDAO;
import com.teenwallet.dao.TransactionDAO;

import javax.swing.*;
import java.awt.*;

public class TeenDashboardFrame extends BaseFrame {
    private User teenUser;
    private VirtualCardPanel cardPanel;
    private JLabel welcomeLabel;
    private JLabel cardStatusLabel;

    public TeenDashboardFrame(User teenUser) {
        super(teenUser.getUsername() + "'s Dashboard");
        this.teenUser = teenUser;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        JPanel welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        cardPanel = new VirtualCardPanel(teenUser.getUsername());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        centerPanel.add(cardPanel, gbc);

        JPanel statusPanel = createCardStatusPanel();
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        centerPanel.add(statusPanel, gbc);

        JPanel actionsPanel = createQuickActionsPanel();
        gbc.gridy = 2;
        gbc.weighty = 0.3;
        centerPanel.add(actionsPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContent(mainPanel);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        welcomeLabel = new JLabel("Welcome back, " + teenUser.getUsername() + "! 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        panel.add(dateLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCardStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        boolean isLocked = SettingsDAO.isCardLockedForUser(teenUser.getUsername());
        cardStatusLabel = new JLabel(isLocked ? "🔒 Card is Locked" : "✅ Card is Active");
        cardStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardStatusLabel.setForeground(isLocked ? Color.RED : new Color(76, 175, 80));
        panel.add(cardStatusLabel);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton paymentBtn = createActionButton("💰 Make Payment",
                "Make a payment using your wallet", new Color(76, 175, 80));
        paymentBtn.addActionListener(e -> openPaymentFrame());
        panel.add(paymentBtn);

        JButton goalsBtn = createActionButton("🎯 My Goals",
                "View and manage savings goals", new Color(33, 150, 243));
        goalsBtn.addActionListener(e -> new GoalsFrame(teenUser.getUsername()).setVisible(true));
        panel.add(goalsBtn);

        JButton historyBtn = createActionButton("📋 History",
                "View transaction history", new Color(255, 152, 0));
        historyBtn.addActionListener(e -> new TransactionHistoryFrame(teenUser.getUsername()).setVisible(true));
        panel.add(historyBtn);

        JButton reportsBtn = createActionButton("📊 Reports",
                "View spending reports", new Color(156, 39, 176));
        reportsBtn.addActionListener(e -> new ReportsFrame().setVisible(true));
        panel.add(reportsBtn);

        return panel;
    }

    private JButton createActionButton(String text, String tooltip, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // Force white text
        button.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void openPaymentFrame() {
        PaymentFrame paymentFrame = new PaymentFrame(teenUser.getUsername());
        paymentFrame.setVisible(true);
        paymentFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refreshData();
            }
        });
    }

    public void refreshData() {
        cardPanel.updateBalance();

        boolean isLocked = SettingsDAO.isCardLockedForUser(teenUser.getUsername());
        cardStatusLabel.setText(isLocked ? "🔒 Card is Locked" : "✅ Card is Active");
        cardStatusLabel.setForeground(isLocked ? Color.RED : new Color(76, 175, 80));
    }
}