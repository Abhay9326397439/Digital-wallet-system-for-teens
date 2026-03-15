package com.teenwallet.ui;

import com.teenwallet.model.SavingsGoal;
import com.teenwallet.service.AuthService;
import com.teenwallet.service.WalletService;
import com.teenwallet.dao.SavingsGoalDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GoalsFrame extends JFrame {
    private JTable goalsTable;
    private DefaultTableModel tableModel;
    private JPanel progressPanel;
    private boolean isParent;
    private String username;
    private SavingsGoalDAO goalDAO;

    public GoalsFrame() {
        this(AuthService.getCurrentUser().getUsername());
    }

    public GoalsFrame(String username) {
        this.username = username;
        this.isParent = AuthService.isParent();
        this.goalDAO = new SavingsGoalDAO();

        setTitle("Savings Goals - " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadGoals();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Goal Name", "Target", "Current", "Progress", "Days Left", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        goalsTable = new JTable(tableModel);
        goalsTable.setRowHeight(35);
        goalsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        goalsTable.getTableHeader().setForeground(Color.WHITE);

        goalsTable.getColumnModel().getColumn(3).setCellRenderer(new ProgressBarRenderer());

        JScrollPane scrollPane = new JScrollPane(goalsTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        progressPanel = createProgressPanel();
        centerPanel.add(progressPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Savings Goals");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel(isParent ? "Parent View - " + username : "My Savings Goals");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        header.add(subtitleLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

        if (!isParent) {
            JButton addGoalButton = new JButton("➕ New Goal");
            addGoalButton.setBackground(new Color(144, 238, 144)); // Light green
            addGoalButton.setForeground(Color.BLACK); // BLACK text
            addGoalButton.setFocusPainted(false);
            addGoalButton.addActionListener(e -> showAddGoalDialog());
            panel.add(addGoalButton);

            JButton transferButton = new JButton("💰 Transfer to Goal");
            transferButton.setBackground(new Color(173, 216, 230)); // Light blue
            transferButton.setForeground(Color.BLACK); // BLACK text
            transferButton.setFocusPainted(false);
            transferButton.addActionListener(e -> showTransferDialog());
            panel.add(transferButton);
        } else {
            JButton bonusButton = new JButton("🎁 Give Bonus");
            bonusButton.setBackground(new Color(255, 218, 185)); // Peach
            bonusButton.setForeground(Color.BLACK); // BLACK text
            bonusButton.setFocusPainted(false);
            bonusButton.addActionListener(e -> showBonusDialog());
            panel.add(bonusButton);
        }

        JButton refreshButton = new JButton("↻ Refresh");
        refreshButton.setBackground(new Color(230, 230, 250)); // Lavender
        refreshButton.setForeground(Color.BLACK); // BLACK text
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadGoals());
        panel.add(refreshButton);

        return panel;
    }

    private void loadGoals() {
        tableModel.setRowCount(0);
        List<SavingsGoal> goals = goalDAO.getGoalsForUser(username);

        int totalGoals = goals.size();
        int completed = 0;
        double totalSaved = 0;

        for (SavingsGoal goal : goals) {
            String status = goal.isCompleted() ? "✓ Completed" :
                    (goal.getDaysLeft() < 0 ? "⚠ Overdue" : "🔄 In Progress");

            tableModel.addRow(new Object[]{
                    goal.getName(),
                    "₹" + String.format("%.2f", goal.getTargetAmount()),
                    "₹" + String.format("%.2f", goal.getCurrentAmount()),
                    goal.getProgressPercentage(),
                    goal.getDaysLeft() + " days",
                    status,
                    goal
            });

            if (goal.isCompleted()) completed++;
            totalSaved += goal.getCurrentAmount();
        }

        updateProgressStats(totalGoals, completed, totalSaved);
        checkGoalAlerts(goals);
    }

    private void updateProgressStats(int total, int completed, double totalSaved) {
        progressPanel.removeAll();
        progressPanel.add(createStatPanel("Total Goals", String.valueOf(total)));
        progressPanel.add(createStatPanel("Completed", String.valueOf(completed)));
        progressPanel.add(createStatPanel("Total Saved", "₹" + String.format("%.2f", totalSaved)));
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private JPanel createStatPanel(String label, String value) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(new Color(70, 130, 180));
        panel.add(valueLabel);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(nameLabel);

        return panel;
    }

    private void checkGoalAlerts(List<SavingsGoal> goals) {
        for (SavingsGoal goal : goals) {
            double percent = goal.getProgressPercentage();

            if (percent >= 100 && !goal.isCompleted()) {
                JOptionPane.showMessageDialog(this,
                        "🎉 Congratulations! Goal '" + goal.getName() + "' is complete!",
                        "Goal Completed",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (percent >= 80 && percent < 100) {
                JOptionPane.showMessageDialog(this,
                        "🎯 Goal '" + goal.getName() + "' is " + String.format("%.1f", percent) + "% complete!\nAlmost there!",
                        "Goal Progress",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showAddGoalDialog() {
        JDialog dialog = new JDialog(this, "Create New Goal", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(15);
        JTextField targetField = new JTextField(15);
        JTextField dateField = new JTextField(15);
        dateField.setText(LocalDate.now().plusMonths(1).toString());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Goal Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Target Amount (₹):"), gbc);
        gbc.gridx = 1;
        panel.add(targetField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Target Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        JButton createButton = new JButton("Create Goal");
        createButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double target = Double.parseDouble(targetField.getText().trim());
                LocalDate targetDate = LocalDate.parse(dateField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a goal name");
                    return;
                }

                SavingsGoal goal = new SavingsGoal(0, name, target, 0, targetDate, false, username);
                if (goalDAO.addGoal(goal)) {
                    dialog.dispose();
                    loadGoals();
                    JOptionPane.showMessageDialog(this, "Goal created successfully!");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount format");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(createButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showTransferDialog() {
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a goal first");
            return;
        }

        SavingsGoal goal = (SavingsGoal) tableModel.getValueAt(selectedRow, 6);

        String amountStr = JOptionPane.showInputDialog(this,
                "Enter amount to transfer to '" + goal.getName() + "':",
                "Transfer to Goal",
                JOptionPane.QUESTION_MESSAGE);

        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive");
                    return;
                }

                boolean success = WalletService.transferToGoal(goal.getId(), amount, username);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Transfer successful!");
                    loadGoals();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount");
            }
        }
    }

    private void showBonusDialog() {
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a goal first");
            return;
        }

        SavingsGoal goal = (SavingsGoal) tableModel.getValueAt(selectedRow, 6);

        String amountStr = JOptionPane.showInputDialog(this,
                "Enter bonus amount for '" + goal.getName() + "':",
                "Parent Bonus",
                JOptionPane.QUESTION_MESSAGE);

        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive");
                    return;
                }

                boolean success = WalletService.parentBonusToGoal(goal.getId(), amount, username);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Bonus added successfully!");
                    loadGoals();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount");
            }
        }
    }

    class ProgressBarRenderer extends JProgressBar implements javax.swing.table.TableCellRenderer {
        public ProgressBarRenderer() {
            super(0, 100);
            setStringPainted(true);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            double progress = (double) value;
            setValue((int) Math.round(progress));
            setString(String.format("%.1f%%", progress));

            if (progress >= 100) {
                setForeground(new Color(76, 175, 80));
            } else if (progress >= 80) {
                setForeground(new Color(33, 150, 243));
            } else if (progress >= 50) {
                setForeground(new Color(255, 152, 0));
            } else {
                setForeground(new Color(244, 67, 54));
            }

            return this;
        }
    }
}