package com.teenwallet.ui;

import com.teenwallet.model.Transaction;
import com.teenwallet.service.AuthService;
import com.teenwallet.utils.FileManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;

public class ReportsFrame extends JFrame {
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private JLabel totalSpendingLabel;
    private JLabel categoryBreakdownLabel;
    private JPanel chartPanel;
    private JPanel alertPanel;

    public ReportsFrame() {
        setTitle("Monthly Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        initComponents();
        loadReport();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header with filters
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top panel with chart and alerts
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(240, 248, 255));

        // Alert panel
        alertPanel = createAlertPanel();
        topPanel.add(alertPanel, BorderLayout.NORTH);

        // Chart panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Category-wise Spending"));
        topPanel.add(chartPanel, BorderLayout.CENTER);

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

        JLabel titleLabel = new JLabel("Monthly Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        header.add(new JLabel("Month:"));
        monthCombo = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        header.add(monthCombo);

        header.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 2; y <= currentYear + 1; y++) {
            yearCombo.addItem(y);
        }
        yearCombo.setSelectedItem(currentYear);
        header.add(yearCombo);

        JButton loadButton = new JButton("Load Report");
        loadButton.addActionListener(e -> loadReport());
        header.add(loadButton);

        if (AuthService.isParent()) {
            JButton exportButton = new JButton("Export to CSV");
            exportButton.addActionListener(e -> exportToCSV());
            header.add(exportButton);
        }

        return header;
    }

    private JPanel createAlertPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(255, 243, 205));
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7)));
        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Transactions"));

        String[] columns = {"Date", "Type", "Amount", "Category", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(25);
        transactionsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        transactionsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setPreferredSize(new Dimension(800, 200));
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
        panel.add(totalSpendingLabel);

        categoryBreakdownLabel = new JLabel("Category Breakdown: ");
        categoryBreakdownLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(categoryBreakdownLabel);

        JLabel savingsLabel = new JLabel("Savings Progress: ");
        savingsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(savingsLabel);

        return panel;
    }

    private void loadReport() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (int) yearCombo.getSelectedItem();

        YearMonth selectedMonth = YearMonth.of(year, month);
        LocalDate startDate = selectedMonth.atDay(1);
        LocalDate endDate = selectedMonth.atEndOfMonth();

        List<Transaction> allTransactions = FileManager.loadTransactions();
        List<Transaction> monthlyTransactions = new ArrayList<>();
        Map<String, Double> categorySpending = new HashMap<>();
        double totalSpending = 0;

        // Filter transactions for selected month
        for (Transaction t : allTransactions) {
            LocalDate transDate = t.getDate().toLocalDate();
            if (!transDate.isBefore(startDate) && !transDate.isAfter(endDate)) {
                monthlyTransactions.add(t);

                if (t.getType() == Transaction.TransactionType.DEBIT) {
                    totalSpending += t.getAmount();
                    categorySpending.merge(t.getCategory(), t.getAmount(), Double::sum);
                }
            }
        }

        // Update table
        updateTransactionsTable(monthlyTransactions);

        // Update summary
        totalSpendingLabel.setText(String.format("Total Spending: ₹%.2f", totalSpending));

        // Update category breakdown
        StringBuilder breakdown = new StringBuilder("<html>Category Breakdown:<br>");
        for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
            breakdown.append(String.format("%s: ₹%.2f<br>", entry.getKey(), entry.getValue()));
        }
        breakdown.append("</html>");
        categoryBreakdownLabel.setText(breakdown.toString());

        // Update chart
        updateChart(categorySpending);

        // Check alerts
        checkAlerts(monthlyTransactions, totalSpending);
    }

    private void updateTransactionsTable(List<Transaction> transactions) {
        tableModel.setRowCount(0);

        // Sort by date descending
        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                    t.getFormattedDate(),
                    t.getType(),
                    t.getFormattedAmount(),
                    t.getCategory(),
                    t.getDescription()
            });
        }
    }

    private void updateChart(Map<String, Double> categorySpending) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
            dataset.addValue(entry.getValue(), "Spending", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Category-wise Spending",
                "Category",
                "Amount (₹)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Customize chart colors
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setBackgroundPaint(new Color(240, 248, 255));

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(barChart), BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void checkAlerts(List<Transaction> transactions, double totalSpending) {
        alertPanel.removeAll();

        double weeklyLimit = 1500; // Default weekly limit
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
                JLabel alert = new JLabel("⚠ Warning: Weekly spending is above 90% of limit!");
                alert.setForeground(new Color(244, 67, 54));
                alertPanel.add(alert);
            }

            // Check category limits
            Map<String, Double> categoryLimits = FileManager.loadSettings().getCategoryLimits();
            for (Map.Entry<String, Double> entry : categoryLimits.entrySet()) {
                double spent = transactions.stream()
                        .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                        .filter(t -> t.getCategory().equals(entry.getKey()))
                        .mapToDouble(Transaction::getAmount)
                        .sum();

                if (spent > entry.getValue() * 0.8) {
                    JLabel alert = new JLabel(String.format(
                            "⚠ %s spending is above 80%% of limit (₹%.2f/₹%.2f)",
                            entry.getKey(), spent, entry.getValue()));
                    alert.setForeground(new Color(255, 152, 0));
                    alertPanel.add(alert);
                }
            }
        }

        if (alertPanel.getComponentCount() == 0) {
            JLabel noAlert = new JLabel("✓ All limits are within normal range");
            noAlert.setForeground(new Color(76, 175, 80));
            alertPanel.add(noAlert);
        }

        alertPanel.revalidate();
        alertPanel.repaint();
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("monthly_report.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                // Write header
                writer.println("Date,Type,Amount,Category,Description");

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.println(
                            tableModel.getValueAt(i, 0) + "," +
                                    tableModel.getValueAt(i, 1) + "," +
                                    tableModel.getValueAt(i, 2) + "," +
                                    tableModel.getValueAt(i, 3) + "," +
                                    tableModel.getValueAt(i, 4)
                    );
                }

                JOptionPane.showMessageDialog(this, "Report exported successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage());
            }
        }
    }
}