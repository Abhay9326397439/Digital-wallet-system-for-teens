package com.teenwallet.ui;

import com.teenwallet.service.AuthService;
import com.teenwallet.service.WalletService;
import com.teenwallet.ui.components.SideNavigationPanel;
import com.teenwallet.ui.components.VirtualCardPanel;

import javax.swing.*;
import java.awt.*;

public class TeenDashboardFrame extends JFrame {
    private VirtualCardPanel cardPanel;
    private JLabel welcomeLabel;
    private JPanel mainContentPanel;

    public TeenDashboardFrame() {
        setTitle("TeenWallet Manager - Teen Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        initComponents();
    }

    private void initComponents() {
        // Main layout
        setLayout(new BorderLayout());

        // Welcome header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Side navigation
        SideNavigationPanel navPanel = new SideNavigationPanel(this);
        add(navPanel, BorderLayout.WEST);

        // Main content area with card
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(240, 248, 255));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Virtual card
        cardPanel = new VirtualCardPanel();
        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cardWrapper.setOpaque(false);
        cardWrapper.add(cardPanel);
        mainContentPanel.add(cardWrapper, BorderLayout.CENTER);

        // Quick actions panel
        JPanel actionsPanel = createQuickActionsPanel();
        mainContentPanel.add(actionsPanel, BorderLayout.SOUTH);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        welcomeLabel = new JLabel("Welcome, " + AuthService.getCurrentUser().getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        header.add(welcomeLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        header.add(dateLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[][] actions = {
                {"💰", "Make Payment", "Make a payment"},
                {"📊", "Reports", "View reports"},
                {"🎯", "My Goals", "Savings goals"},
                {"📋", "History", "Transaction history"}
        };

        for (String[] action : actions) {
            JButton button = createActionButton(action[0], action[1], action[2]);
            panel.add(button);
        }

        return panel;
    }

    private JButton createActionButton(String icon, String title, String tooltip) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + title + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(70, 130, 180));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listeners
        if (title.equals("Make Payment")) {
            button.addActionListener(e -> openPaymentFrame());
        } else if (title.equals("Reports")) {
            button.addActionListener(e -> new ReportsFrame().setVisible(true));
        } else if (title.equals("My Goals")) {
            button.addActionListener(e -> new GoalsFrame().setVisible(true));
        } else if (title.equals("History")) {
            button.addActionListener(e -> new TransactionHistoryFrame().setVisible(true));
        }

        return button;
    }

    private void openPaymentFrame() {
        PaymentFrame paymentFrame = new PaymentFrame();
        paymentFrame.setVisible(true);
    }

    public void refreshBalance() {
        cardPanel.updateBalance();
    }
}