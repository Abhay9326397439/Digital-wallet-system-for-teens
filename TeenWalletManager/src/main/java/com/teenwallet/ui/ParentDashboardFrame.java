package com.teenwallet.ui;

import com.teenwallet.service.AuthService;
import com.teenwallet.service.WalletService;
import com.teenwallet.dao.UserDAO;
import com.teenwallet.dao.SettingsDAO;
import com.teenwallet.dao.TransactionDAO;
import com.teenwallet.model.User;
import com.teenwallet.model.Transaction;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ParentDashboardFrame extends BaseFrame {
    private JComboBox<String> teenSelector;
    private JLabel balanceLabel;
    private JTable recentTransactionsTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;

    public ParentDashboardFrame() {
        super("Parent Dashboard");
        userDAO = new UserDAO();
        initComponents();
        loadTeens();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createQuickActionsPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContent(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel selectLabel = new JLabel("Select Teen:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(selectLabel);

        teenSelector = new JComboBox<>();
        teenSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        teenSelector.setPreferredSize(new Dimension(150, 30));
        teenSelector.addActionListener(e -> onTeenSelected());
        panel.add(teenSelector);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> onTeenSelected());
        panel.add(refreshBtn);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setOpaque(false);

        JButton addMoneyBtn = createActionButton("➕ Add Money", new Color(76, 175, 80));
        addMoneyBtn.addActionListener(e -> openAddMoney());
        actionPanel.add(addMoneyBtn);

        JButton lockCardBtn = createActionButton("🔒 Lock/Unlock Card", new Color(255, 152, 0));
        lockCardBtn.addActionListener(e -> toggleCardLock());
        actionPanel.add(lockCardBtn);

        JButton viewHistoryBtn = createActionButton("📋 View History", new Color(33, 150, 243));
        viewHistoryBtn.addActionListener(e -> openTransactionHistory());
        actionPanel.add(viewHistoryBtn);

        gbc.gridy = 2;
        panel.add(actionPanel, gbc);

        JPanel tablePanel = createTransactionsPanel();
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(tablePanel, gbc);

        return panel;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // Force white text
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Recent Transactions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

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

        JScrollPane scrollPane = new JScrollPane(recentTransactionsTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[][] actions = {
                {"📊", "Reports", "View detailed reports"},
                {"🎯", "Goals", "View teen's savings goals"},
                {"⚙", "Settings", "Manage limits"},
                {"👥", "Manage Teens", "Add/Remove teen accounts"}
        };

        for (String[] action : actions) {
            JButton button = createQuickActionButton(action[0], action[1], action[2]);
            panel.add(button);
        }

        return panel;
    }

    private JButton createQuickActionButton(String icon, String title, String tooltip) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + title + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180)); // Steel blue background
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 25, 112), 1), // Dark blue border
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Cornflower blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE); // Back to steel blue
            }
        });

        if (title.equals("Reports")) {
            button.addActionListener(e -> new ReportsFrame().setVisible(true));
        } else if (title.equals("Goals")) {
            button.addActionListener(e -> new GoalsFrame().setVisible(true));
        } else if (title.equals("Settings")) {
            button.addActionListener(e -> openSettings());
        } else if (title.equals("Manage Teens")) {
            button.addActionListener(e -> showManageTeensDialog());
        }

        return button;
    }

    private void loadTeens() {
        teenSelector.removeAllItems();
        List<User> teens = userDAO.getAllTeenUsers();

        if (teens.isEmpty()) {
            teenSelector.addItem("teen1");
            teenSelector.addItem("teen2");
            teenSelector.addItem("teen3");
        } else {
            for (User teen : teens) {
                teenSelector.addItem(teen.getUsername());
            }
        }

        if (teenSelector.getItemCount() > 0) {
            teenSelector.setSelectedIndex(0);
            onTeenSelected();
        }
    }

    private void onTeenSelected() {
        String teenUsername = (String) teenSelector.getSelectedItem();
        if (teenUsername != null) {
            double balance = WalletService.getBalanceForUser(teenUsername);
            balanceLabel.setText(String.format("₹%.2f", balance));
            loadRecentTransactions(teenUsername);
        }
    }

    private void loadRecentTransactions(String teenUsername) {
        tableModel.setRowCount(0);
        List<Transaction> transactions = TransactionDAO.getTransactionsForUser(teenUsername);

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

    private void openAddMoney() {
        String teenUsername = (String) teenSelector.getSelectedItem();
        if (teenUsername != null) {
            AddMoneyFrame addMoneyFrame = new AddMoneyFrame(teenUsername);
            addMoneyFrame.setVisible(true);
            addMoneyFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    onTeenSelected();
                }
            });
        }
    }

    private void toggleCardLock() {
        String teenUsername = (String) teenSelector.getSelectedItem();
        if (teenUsername != null) {
            boolean currentLock = SettingsDAO.isCardLockedForUser(teenUsername);
            SettingsDAO.setCardLockedForUser(teenUsername, !currentLock);
            JOptionPane.showMessageDialog(this,
                    "Card " + (!currentLock ? "locked" : "unlocked") + " for " + teenUsername);
        }
    }

    private void openTransactionHistory() {
        String teenUsername = (String) teenSelector.getSelectedItem();
        if (teenUsername != null) {
            new TransactionHistoryFrame(teenUsername).setVisible(true);
        }
    }

    private void openSettings() {
        String teenUsername = (String) teenSelector.getSelectedItem();
        if (teenUsername != null) {
            new SettingsFrame(teenUsername).setVisible(true);
        }
    }

    private void showManageTeensDialog() {
        JDialog dialog = new JDialog(this, "Manage Teen Accounts", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<User> teens = userDAO.getAllTeenUsers();
        for (User teen : teens) {
            listModel.addElement(teen.getUsername());
        }

        JList<String> teenList = new JList<>(listModel);
        teenList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(teenList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> {
            String selected = teenList.getSelectedValue();
            if (selected != null) {
                if (userDAO.deleteUser(selected)) {
                    listModel.removeElement(selected);
                    JOptionPane.showMessageDialog(dialog, "Teen deleted successfully");
                    loadTeens();
                }
            }
        });
        buttonPanel.add(deleteButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}