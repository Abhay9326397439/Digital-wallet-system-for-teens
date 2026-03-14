package com.teenwallet.ui;

import com.teenwallet.service.AuthService;
import com.teenwallet.service.WalletService;
import com.teenwallet.ui.components.SideNavigationPanel;
import com.teenwallet.utils.FileManager;
import com.teenwallet.model.Transaction;
// Add this import at the top of ParentDashboardFrame.java
import javax.swing.border.TitledBorder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ParentDashboardFrame extends JFrame {
    private JLabel balanceLabel;
    private JTable recentTransactionsTable;
    private DefaultTableModel tableModel;

    public ParentDashboardFrame() {
        setTitle("TeenWallet Manager - Parent Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        initComponents();
        loadRecentTransactions();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Side navigation
        SideNavigationPanel navPanel = new SideNavigationPanel(this);
        add(navPanel, BorderLayout.WEST);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Teen account overview
        JPanel overviewPanel = createOverviewPanel();
        mainPanel.add(overviewPanel, BorderLayout.NORTH);

        // Recent transactions
        JPanel transactionsPanel = createTransactionsPanel();
        mainPanel.add(transactionsPanel, BorderLayout.CENTER);

        // Quick actions
        JPanel actionsPanel = createQuickActionsPanel();
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Parent Dashboard - " + AuthService.getCurrentUser().getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        header.add(welcomeLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        header.add(dateLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel teenWalletLabel = new JLabel("Teen's Wallet Balance");
        teenWalletLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(teenWalletLabel, gbc);

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        balanceLabel.setForeground(new Color(70, 130, 180));
        gbc.gridy = 1;
        panel.add(balanceLabel, gbc);

        JButton refreshButton = new JButton("↻ Refresh");
        refreshButton.addActionListener(e -> refreshData());
        gbc.gridy = 2;
        panel.add(refreshButton, gbc);

        updateBalance();

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Recent Transactions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        // Table
        String[] columns = {"Date", "Type", "Amount", "Category", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentTransactionsTable = new JTable(tableModel);
        recentTransactionsTable.setRowHeight(25);
        recentTransactionsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        recentTransactionsTable.getTableHeader().setForeground(Color.WHITE);
        recentTransactionsTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(recentTransactionsTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        panel.add(scrollPane, BorderLayout.CENTER);

        // View all button
        JButton viewAllButton = new JButton("View All Transactions");
        viewAllButton.addActionListener(e -> new TransactionHistoryFrame().setVisible(true));
        panel.add(viewAllButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[][] actions = {
                {"➕", "Add Money", "Add money to teen's wallet"},
                {"🔒", "Settings", "Manage limits & card lock"},
                {"📊", "Reports", "View detailed reports"},
                {"🎯", "Goals", "View teen's savings goals"}
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
        if (title.equals("Add Money")) {
            button.addActionListener(e -> new AddMoneyFrame().setVisible(true));
        } else if (title.equals("Settings")) {
            button.addActionListener(e -> new SettingsFrame().setVisible(true));
        } else if (title.equals("Reports")) {
            button.addActionListener(e -> new ReportsFrame().setVisible(true));
        } else if (title.equals("Goals")) {
            button.addActionListener(e -> new GoalsFrame().setVisible(true));
        }

        return button;
    }

    private void loadRecentTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = FileManager.loadTransactions();

        // Show only last 10 transactions
        int start = Math.max(0, transactions.size() - 10);
        for (int i = transactions.size() - 1; i >= start; i--) {
            Transaction t = transactions.get(i);
            tableModel.addRow(new Object[]{
                    t.getFormattedDate(),
                    t.getType(),
                    t.getFormattedAmount(),
                    t.getCategory(),
                    t.getDescription()
            });
        }
    }

    private void updateBalance() {
        double balance = WalletService.getBalance();
        balanceLabel.setText(String.format("₹%.2f", balance));
    }

    private void refreshData() {
        updateBalance();
        loadRecentTransactions();
    }
}