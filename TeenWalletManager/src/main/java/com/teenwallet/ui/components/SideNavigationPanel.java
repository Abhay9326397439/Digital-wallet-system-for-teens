package com.teenwallet.ui.components;

import com.teenwallet.service.AuthService;
import com.teenwallet.model.User;
import com.teenwallet.ui.*;

import javax.swing.*;
import java.awt.*;

public class SideNavigationPanel extends JPanel {
    private JFrame parentFrame;

    public SideNavigationPanel(JFrame parent) {
        this.parentFrame = parent;

        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(25, 25, 112));
        setLayout(new GridBagLayout());

        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel logoLabel = new JLabel("💰 TeenWallet");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoLabel.setForeground(new Color(255, 215, 0));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(20, 10, 20, 10);
        add(logoLabel, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);

        add(createNavButton("🏠 Home", this::navigateHome), gbc);

        User currentUser = AuthService.getCurrentUser();
        if (AuthService.isParent()) {
            add(createNavButton("➕ Add Money", this::navigateAddMoney), gbc);
        } else {
            add(createNavButton("💳 Make Payment", this::navigatePayment), gbc);
        }

        add(createNavButton("📊 Reports", this::navigateReports), gbc);
        add(createNavButton("🎯 Goals", this::navigateGoals), gbc);

        if (AuthService.isParent()) {
            add(createNavButton("⚙ Settings", this::navigateSettings), gbc);
        }

        add(createNavButton("📋 History", this::navigateHistory), gbc);

        gbc.weighty = 1;
        add(Box.createVerticalGlue(), gbc);

        gbc.weighty = 0;
        add(createNavButton("🚪 Logout", this::logout), gbc);
    }

    private JButton createNavButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(25, 25, 112));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 25, 112));
            }
        });

        button.addActionListener(e -> action.run());

        return button;
    }

    private void navigateHome() {
        User currentUser = AuthService.getCurrentUser();
        if (AuthService.isParent()) {
            new ParentDashboardFrame().setVisible(true);
        } else {
            new TeenDashboardFrame(currentUser).setVisible(true);
        }
        parentFrame.dispose();
    }

    private void navigateAddMoney() {
        if (parentFrame instanceof ParentDashboardFrame) {
            ParentDashboardFrame parentFrame = (ParentDashboardFrame) this.parentFrame;
            // This will be handled by the parent frame
        }
    }

    private void navigatePayment() {
        User currentUser = AuthService.getCurrentUser();
        if (currentUser != null && currentUser.isTeen()) {
            new PaymentFrame(currentUser.getUsername()).setVisible(true);
        }
    }

    private void navigateReports() {
        new ReportsFrame().setVisible(true);
    }

    private void navigateGoals() {
        User currentUser = AuthService.getCurrentUser();
        new GoalsFrame(currentUser.getUsername()).setVisible(true);
    }

    private void navigateSettings() {
        if (AuthService.isParent()) {
            if (parentFrame instanceof ParentDashboardFrame) {
                ParentDashboardFrame parentFrame = (ParentDashboardFrame) this.parentFrame;
                // This will be handled by the parent frame
            }
        }
    }

    private void navigateHistory() {
        User currentUser = AuthService.getCurrentUser();
        new TransactionHistoryFrame(currentUser.getUsername()).setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.logout();
            parentFrame.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}