package com.teenwallet.ui;

import com.teenwallet.model.Transaction;
import com.teenwallet.service.AuthService;
import com.teenwallet.dao.TransactionDAO;
import com.teenwallet.dao.SettingsDAO;
import com.teenwallet.model.UserSettings;
import com.teenwallet.utils.ExportUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ReportsFrame extends JFrame {
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private JLabel totalSpendingLabel;
    private JLabel alertLabel;
    private JPanel chartPanel;
    private String username;
    private JButton exportButton;
    private JLabel categoryBreakdownLabel;
    private JLabel savingsProgressLabel;
    private boolean isParent;
    private String selectedTeenUsername; // For parent view

    public ReportsFrame() {
        this(AuthService.getCurrentUser().getUsername());
    }

    public ReportsFrame(String username) {
        this.username = username;
        this.isParent = AuthService.isParent();
        setTitle("Monthly Reports - " + (isParent ? "Parent View" : username));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        initComponents();

        // If parent, show teen selector
        if (isParent) {
            showTeenSelector();
        } else {
            loadReport();
        }
    }

    private void showTeenSelector() {
        // Create teen selector dialog
        JDialog dialog = new JDialog(this, "Select Teen", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(new JLabel("Select Teen:"), gbc);

        JComboBox<String> teenCombo = new JComboBox<>();
        List<com.teenwallet.model.User> teens = new com.teenwallet.dao.UserDAO().getAllTeenUsers();
        for (com.teenwallet.model.User teen : teens) {
            teenCombo.addItem(teen.getUsername());
        }
        gbc.gridy = 1;
        panel.add(teenCombo, gbc);

        JButton selectButton = new JButton("View Reports");
        selectButton.setBackground(new Color(70, 130, 180));
        selectButton.setForeground(Color.WHITE);
        selectButton.addActionListener(e -> {
            selectedTeenUsername = (String) teenCombo.getSelectedItem();
            username = selectedTeenUsername;
            loadReport();
            dialog.dispose();
        });
        gbc.gridy = 2;
        panel.add(selectButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header with filters
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top panel with chart and alerts
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                "Category-wise Spending Analysis",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.BLACK
        ));

        // Chart panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(800, 300));
        topPanel.add(chartPanel, BorderLayout.CENTER);

        // Alert panel
        JPanel alertPanel = createAlertPanel();
        topPanel.add(alertPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(topPanel);

        // Bottom panel with transactions
        JPanel bottomPanel = createTransactionsPanel();
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("📊 Monthly Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        if (isParent && selectedTeenUsername != null) {
            JLabel teenLabel = new JLabel("Teen: " + selectedTeenUsername);
            teenLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            teenLabel.setForeground(Color.YELLOW);
            header.add(teenLabel);
        }

        header.add(new JLabel("Month:"));
        monthCombo = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        monthCombo.setBackground(Color.WHITE);
        monthCombo.setForeground(Color.BLACK);
        header.add(monthCombo);

        header.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 2; y <= currentYear + 1; y++) {
            yearCombo.addItem(y);
        }
        yearCombo.setSelectedItem(currentYear);
        yearCombo.setBackground(Color.WHITE);
        yearCombo.setForeground(Color.BLACK);
        header.add(yearCombo);

        JButton loadButton = new JButton("Load Report");
        loadButton.setBackground(new Color(255, 215, 0));
        loadButton.setForeground(Color.BLACK);
        loadButton.setFocusPainted(false);
        loadButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loadButton.addActionListener(e -> loadReport());
        header.add(loadButton);

        JButton refreshButton = new JButton("↻ Refresh");
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.addActionListener(e -> loadReport());
        header.add(refreshButton);

        // Export button for parent
        if (isParent) {
            exportButton = new JButton("📥 Export to CSV");
            exportButton.setBackground(new Color(76, 175, 80));
            exportButton.setForeground(Color.BLACK);
            exportButton.setFocusPainted(false);
            exportButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            exportButton.addActionListener(e -> exportToCSV());
            header.add(exportButton);
        }

        // Back button to select different teen for parent
        if (isParent) {
            JButton changeTeenButton = new JButton("👥 Change Teen");
            changeTeenButton.setBackground(new Color(255, 152, 0));
            changeTeenButton.setForeground(Color.BLACK);
            changeTeenButton.setFocusPainted(false);
            changeTeenButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            changeTeenButton.addActionListener(e -> {
                dispose();
                new ReportsFrame().setVisible(true);
            });
            header.add(changeTeenButton);
        }

        return header;
    }

    private JPanel createAlertPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(255, 255, 224)); // Light yellow
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7)));

        alertLabel = new JLabel("✓ All limits are within normal range");
        alertLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        alertLabel.setForeground(new Color(76, 175, 80));
        panel.add(alertLabel);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                "Monthly Transactions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.BLACK
        ));

        String[] columns = {"Date", "Type", "Amount (₹)", "Category", "Description", "Balance (₹)"};
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
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        transactionsTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Custom renderer for amount columns
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new AmountCellRenderer());
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(new AmountCellRenderer());

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setPreferredSize(new Dimension(900, 250));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        totalSpendingLabel = new JLabel("Total Spending: ₹0.00");
        totalSpendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalSpendingLabel.setForeground(Color.BLACK);
        panel.add(totalSpendingLabel);

        categoryBreakdownLabel = new JLabel("Category Breakdown: ");
        categoryBreakdownLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryBreakdownLabel.setForeground(Color.BLACK);
        panel.add(categoryBreakdownLabel);

        savingsProgressLabel = new JLabel("Savings Progress: ");
        savingsProgressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        savingsProgressLabel.setForeground(Color.BLACK);
        panel.add(savingsProgressLabel);

        return panel;
    }

    private void loadReport() {
        if (username == null || username.isEmpty()) {
            return;
        }

        int month = monthCombo.getSelectedIndex() + 1;
        int year = (int) yearCombo.getSelectedItem();

        YearMonth selectedMonth = YearMonth.of(year, month);
        LocalDate startDate = selectedMonth.atDay(1);
        LocalDate endDate = selectedMonth.atEndOfMonth();

        // Get transactions for the user
        List<Transaction> allTransactions = TransactionDAO.getTransactionsForUser(username);
        List<Transaction> monthlyTransactions = new ArrayList<>();
        Map<String, Double> categorySpending = new HashMap<>();
        double totalSpending = 0;
        double totalCredits = 0;

        System.out.println("Loading report for user: " + username);
        System.out.println("Total transactions found: " + allTransactions.size());

        // Filter transactions for selected month
        for (Transaction t : allTransactions) {
            LocalDate transDate = t.getDate().toLocalDate();
            if (!transDate.isBefore(startDate) && !transDate.isAfter(endDate)) {
                monthlyTransactions.add(t);
                System.out.println("Added transaction: " + t.getDescription() + " - " + t.getAmount());

                if (t.getType() == Transaction.TransactionType.DEBIT) {
                    totalSpending += t.getAmount();
                    categorySpending.merge(t.getCategory(), t.getAmount(), Double::sum);
                } else if (t.getType() == Transaction.TransactionType.CREDIT) {
                    totalCredits += t.getAmount();
                }
            }
        }

        System.out.println("Monthly transactions: " + monthlyTransactions.size());
        System.out.println("Category spending: " + categorySpending);

        // Update table
        updateTransactionsTable(monthlyTransactions);

        // Update summary
        totalSpendingLabel.setText(String.format("Total Spending: ₹%.2f", totalSpending));

        // Update category breakdown
        StringBuilder breakdown = new StringBuilder("<html>Category Breakdown:<br>");
        if (categorySpending.isEmpty()) {
            breakdown.append("No spending this month");
        } else {
            // Sort categories by amount descending
            List<Map.Entry<String, Double>> sortedCategories = new ArrayList<>(categorySpending.entrySet());
            sortedCategories.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            for (Map.Entry<String, Double> entry : sortedCategories) {
                double percentage = (entry.getValue() / totalSpending) * 100;
                breakdown.append(String.format("• %s: ₹%.2f (%.1f%%)<br>",
                        entry.getKey(), entry.getValue(), percentage));
            }
        }
        breakdown.append("</html>");
        categoryBreakdownLabel.setText(breakdown.toString());

        // Update savings progress
        double currentBalance = TransactionDAO.getCurrentBalanceForUser(username);
        savingsProgressLabel.setText(String.format("Current Balance: ₹%.2f | Total Credits: ₹%.2f",
                currentBalance, totalCredits));

        // Update chart
        updateChart(categorySpending);

        // Check alerts
        checkAlerts(monthlyTransactions, totalSpending);
    }

    private void updateTransactionsTable(List<Transaction> transactions) {
        tableModel.setRowCount(0);

        // Sort by date descending (newest first)
        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        if (transactions.isEmpty()) {
            tableModel.addRow(new Object[]{"No transactions for this period", "", "", "", "", ""});
        } else {
            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                        t.getFormattedDate(),
                        t.getType().toString(),
                        t.getAmount(),
                        t.getCategory(),
                        t.getDescription(),
                        t.getBalanceAfter()
                });
            }
        }
    }

    private void updateChart(Map<String, Double> categorySpending) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (categorySpending.isEmpty()) {
            // Add dummy data to show empty chart
            dataset.addValue(0.0, "Spending", "No Data");
        } else {
            // Sort categories by amount for better visualization
            List<Map.Entry<String, Double>> sortedCategories = new ArrayList<>(categorySpending.entrySet());
            sortedCategories.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            for (Map.Entry<String, Double> entry : sortedCategories) {
                dataset.addValue(entry.getValue(), "Spending", entry.getKey());
            }
        }

        String chartTitle = isParent ?
                "Category-wise Spending - " + username :
                "Category-wise Spending";

        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "Category",
                "Amount (₹)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Customize chart colors
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getTitle().setPaint(Color.BLACK);
        barChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 248, 255));
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.GRAY);

        // Customize bar colors - use different colors for parent view
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        if (isParent) {
            // Use gradient colors for parent view
            Paint[] colors = {
                    new Color(70, 130, 180),  // Steel blue
                    new Color(76, 175, 80),   // Green
                    new Color(255, 152, 0),   // Orange
                    new Color(156, 39, 176),  // Purple
                    new Color(233, 30, 99),   // Pink
                    new Color(0, 150, 136)    // Teal
            };

            for (int i = 0; i < dataset.getRowCount(); i++) {
                renderer.setSeriesPaint(i, colors[i % colors.length]);
            }
        } else {
            renderer.setSeriesPaint(0, new Color(70, 130, 180));
        }

        renderer.setDrawBarOutline(true);
        renderer.setMaximumBarWidth(0.1);

        // Customize axis labels
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.getDomainAxis().setTickLabelPaint(Color.BLACK);

        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.getRangeAxis().setTickLabelPaint(Color.BLACK);

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(barChart), BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void checkAlerts(List<Transaction> transactions, double totalSpending) {
        if (!isParent) {
            // Only show alerts for teen view
            UserSettings settings = SettingsDAO.getSettingsForUser(username);
            double weeklyLimit = settings.getWeeklyLimit();
            double weeklySpent = 0;

            // Calculate last 7 days spending
            LocalDate weekAgo = LocalDate.now().minusDays(7);
            for (Transaction t : transactions) {
                if (t.getDate().toLocalDate().isAfter(weekAgo) && t.getType() == Transaction.TransactionType.DEBIT) {
                    weeklySpent += t.getAmount();
                }
            }

            if (totalSpending > 0) {
                if (weeklySpent > weeklyLimit * 0.9) {
                    alertLabel.setText("⚠ Warning: Weekly spending is above 90% of limit! (₹" +
                            String.format("%.2f", weeklySpent) + "/₹" + String.format("%.2f", weeklyLimit) + ")");
                    alertLabel.setForeground(new Color(244, 67, 54));
                } else {
                    alertLabel.setText("✓ All limits are within normal range");
                    alertLabel.setForeground(new Color(76, 175, 80));
                }
            } else {
                alertLabel.setText("ℹ No transactions for this period");
                alertLabel.setForeground(new Color(33, 150, 243));
            }
        } else {
            // Parent view - show simple info
            if (totalSpending > 0) {
                alertLabel.setText("📊 Report for " + username + " - Total spent: ₹" +
                        String.format("%.2f", totalSpending));
                alertLabel.setForeground(new Color(70, 130, 180));
            } else {
                alertLabel.setText("ℹ No transactions for this period");
                alertLabel.setForeground(new Color(33, 150, 243));
            }
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("monthly_report_" + username + "_" +
                monthCombo.getSelectedItem() + "_" + yearCombo.getSelectedItem() + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Get current report data
                int month = monthCombo.getSelectedIndex() + 1;
                int year = (int) yearCombo.getSelectedItem();
                List<Transaction> allTransactions = TransactionDAO.getTransactionsForUser(username);
                List<Transaction> monthlyTransactions = new ArrayList<>();

                YearMonth selectedMonth = YearMonth.of(year, month);
                LocalDate startDate = selectedMonth.atDay(1);
                LocalDate endDate = selectedMonth.atEndOfMonth();

                for (Transaction t : allTransactions) {
                    LocalDate transDate = t.getDate().toLocalDate();
                    if (!transDate.isBefore(startDate) && !transDate.isAfter(endDate)) {
                        monthlyTransactions.add(t);
                    }
                }

                // Export to CSV
                boolean success = ExportUtils.exportTransactionsToCSV(monthlyTransactions,
                        fileChooser.getSelectedFile().getAbsolutePath());

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Report exported successfully!\n" + fileChooser.getSelectedFile().getName(),
                            "Export Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to export report",
                            "Export Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting report: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
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
            } else {
                setHorizontalAlignment(JLabel.LEFT);
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

            // Handle selection
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
            } else {
                c.setBackground(Color.WHITE);
            }

            return c;
        }
    }
}