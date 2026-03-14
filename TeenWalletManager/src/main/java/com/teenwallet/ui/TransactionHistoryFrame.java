package com.teenwallet.ui;

import com.teenwallet.dao.TransactionDAO;
import com.teenwallet.model.Transaction;
import com.teenwallet.service.AuthService;
import com.teenwallet.utils.ExportUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
// Add these imports at the top of TransactionHistoryFrame.java
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;

public class TransactionHistoryFrame extends JFrame {
    private TransactionDAO transactionDAO;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JLabel totalLabel;
    private JLabel creditLabel;
    private JLabel debitLabel;

    public TransactionHistoryFrame() {
        setTitle("Transaction History");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        transactionDAO = new TransactionDAO();

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

        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Search transactions...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        searchPanel.add(searchField);

        filterCombo = new JComboBox<>(new String[]{"All", "Today", "This Week", "This Month", "Credits Only", "Debits Only"});
        filterCombo.addActionListener(e -> filterTable());
        searchPanel.add(filterCombo);

        if (AuthService.isParent()) {
            JButton exportButton = new JButton("Export to CSV");
            exportButton.addActionListener(e -> exportToCSV());
            searchPanel.add(exportButton);
        }

        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Create table model
        String[] columns = {"Date", "Type", "Amount", "Category", "Description", "Balance After"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Double.class;
                return String.class;
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(30);
        transactionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        transactionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        transactionsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        transactionsTable.getTableHeader().setForeground(Color.WHITE);
        transactionsTable.setSelectionBackground(new Color(173, 216, 230));

        // Set custom cell renderer for amount column
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new AmountCellRenderer());

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
        panel.add(totalLabel);

        creditLabel = new JLabel("Total Credits: ₹0.00");
        creditLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        creditLabel.setForeground(new Color(76, 175, 80));
        panel.add(creditLabel);

        debitLabel = new JLabel("Total Debits: ₹0.00");
        debitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        debitLabel.setForeground(new Color(244, 67, 54));
        panel.add(debitLabel);

        JLabel balanceLabel = new JLabel("Current Balance: ₹" +
                String.format("%.2f", transactionDAO.getCurrentBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        balanceLabel.setForeground(new Color(70, 130, 180));
        panel.add(balanceLabel);

        return panel;
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getAllTransactions();

        double totalCredits = 0;
        double totalDebits = 0;

        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                    t.getFormattedDate(),
                    t.getType(),
                    t.getAmount(),
                    t.getCategory(),
                    t.getDescription(),
                    String.format("₹%.2f", t.getBalanceAfter())
            });

            if (t.getType() == Transaction.TransactionType.CREDIT) {
                totalCredits += t.getAmount();
            } else if (t.getType() == Transaction.TransactionType.DEBIT) {
                totalDebits += t.getAmount();
            }
        }

        // Update summary
        totalLabel.setText("Total Transactions: " + transactions.size());
        creditLabel.setText(String.format("Total Credits: ₹%.2f", totalCredits));
        debitLabel.setText(String.format("Total Debits: ₹%.2f", totalDebits));
    }

    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        String filter = (String) filterCombo.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Text search filter
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 0, 3, 4)); // Search in date, category, description
        }

        // Date/Type filter
        if (!"All".equals(filter)) {
            switch (filter) {
                case "Today":
                    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    filters.add(RowFilter.regexFilter(today, 0));
                    break;
                case "This Week":
                    // Complex date filtering would require custom comparator
                    break;
                case "This Month":
                    String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                    filters.add(RowFilter.regexFilter(thisMonth, 0));
                    break;
                case "Credits Only":
                    filters.add(RowFilter.regexFilter("CREDIT", 1));
                    break;
                case "Debits Only":
                    filters.add(RowFilter.regexFilter("DEBIT", 1));
                    break;
            }
        }

        if (!filters.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("transactions_export.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ExportUtils.exportTableToCSV(transactionsTable, fileChooser.getSelectedFile().getPath());
            JOptionPane.showMessageDialog(this, "Transactions exported successfully!");
        }
    }

    // Custom cell renderer for amount column
    class AmountCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String type = (String) table.getValueAt(row, 1);
            if ("CREDIT".equals(type)) {
                c.setForeground(new Color(76, 175, 80)); // Green
            } else if ("DEBIT".equals(type)) {
                c.setForeground(new Color(244, 67, 54)); // Red
            } else {
                c.setForeground(new Color(255, 152, 0)); // Orange for savings
            }

            setHorizontalAlignment(JLabel.RIGHT);
            return c;
        }
    }
}