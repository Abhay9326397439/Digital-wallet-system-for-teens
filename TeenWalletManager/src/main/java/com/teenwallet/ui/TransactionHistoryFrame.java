package com.teenwallet.ui;

import com.teenwallet.dao.TransactionDAO;
import com.teenwallet.model.Transaction;
import com.teenwallet.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryFrame extends JFrame {
    private String username;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JLabel totalLabel;
    private JLabel creditLabel;
    private JLabel debitLabel;
    private JLabel balanceLabel;

    public TransactionHistoryFrame(String username) {
        this.username = username;
        setTitle("Transaction History - " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadTransactions();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center - Transactions Table
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom - Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("📋 Transaction History - " + username);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        searchPanel.add(new JLabel("🔍 Search:"));
        searchField = new JTextField(15);
        searchField.setToolTipText("Search by category or description");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        searchPanel.add(searchField);

        JButton refreshButton = new JButton("↻ Refresh");
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.addActionListener(e -> loadTransactions());
        searchPanel.add(refreshButton);

        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Create table model
        String[] columns = {"Date", "Type", "Amount", "Category", "Description", "Balance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(30);
        transactionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        transactionsTable.setForeground(Color.BLACK);
        transactionsTable.setBackground(Color.WHITE);
        transactionsTable.setGridColor(new Color(200, 200, 200));
        transactionsTable.setSelectionBackground(new Color(173, 216, 230));

        // Style the table header
        JTableHeader header = transactionsTable.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Set column widths
        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        transactionsTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Set custom cell renderer for amount columns
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new AmountCellRenderer());
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(new AmountCellRenderer());

        // Add sorter
        sorter = new TableRowSorter<>(tableModel);
        transactionsTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        totalLabel = new JLabel("Total Transactions: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalLabel.setForeground(Color.BLACK);
        panel.add(totalLabel);

        creditLabel = new JLabel("Total Credits: ₹0.00");
        creditLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        creditLabel.setForeground(new Color(76, 175, 80));
        panel.add(creditLabel);

        debitLabel = new JLabel("Total Debits: ₹0.00");
        debitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        debitLabel.setForeground(new Color(244, 67, 54));
        panel.add(debitLabel);

        balanceLabel = new JLabel("Current Balance: ₹0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        balanceLabel.setForeground(new Color(70, 130, 180));
        panel.add(balanceLabel);

        return panel;
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = TransactionDAO.getTransactionsForUser(username);

        double totalCredits = 0;
        double totalDebits = 0;

        if (transactions.isEmpty()) {
            tableModel.addRow(new Object[]{"No transactions found", "", "", "", "", ""});
        } else {
            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                        t.getFormattedDate(),
                        t.getType(),
                        t.getAmount(),
                        t.getCategory(),
                        t.getDescription(),
                        t.getBalanceAfter()
                });

                if (t.getType() == Transaction.TransactionType.CREDIT) {
                    totalCredits += t.getAmount();
                } else if (t.getType() == Transaction.TransactionType.DEBIT) {
                    totalDebits += t.getAmount();
                }
            }
        }

        // Update summary
        double currentBalance = TransactionDAO.getCurrentBalanceForUser(username);
        totalLabel.setText("Total Transactions: " + transactions.size());
        creditLabel.setText(String.format("Total Credits: ₹%.2f", totalCredits));
        debitLabel.setText(String.format("Total Debits: ₹%.2f", totalDebits));
        balanceLabel.setText(String.format("Current Balance: ₹%.2f", currentBalance));
    }

    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 3, 4)); // Search in category and description
        } else {
            sorter.setRowFilter(null);
        }
    }

    // Custom cell renderer for amount column
    class AmountCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Double) {
                setText(String.format("₹%.2f", (Double) value));
                setHorizontalAlignment(JLabel.RIGHT);
            }

            // Color code based on transaction type
            if (column == 2) { // Amount column
                String type = table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "";
                if ("CREDIT".equals(type)) {
                    c.setForeground(new Color(76, 175, 80)); // Green
                } else if ("DEBIT".equals(type)) {
                    c.setForeground(new Color(244, 67, 54)); // Red
                } else {
                    c.setForeground(new Color(255, 152, 0)); // Orange for savings
                }
            } else if (column == 5) { // Balance column
                c.setForeground(new Color(70, 130, 180)); // Blue
            } else {
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    }
}