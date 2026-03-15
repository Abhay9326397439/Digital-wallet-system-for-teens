package com.teenwallet.ui;

import com.teenwallet.service.AuthService;
import com.teenwallet.model.User;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {

    protected JButton homeButton;
    protected JButton backButton;
    protected JPanel mainContentPanel;

    public BaseFrame(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        initBaseComponents();
    }

    private void initBaseComponents() {
        setLayout(new BorderLayout());

        // Top navigation bar
        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH);

        // Main content area
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(240, 248, 255));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(70, 130, 180));
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Left side - Navigation buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        homeButton = createNavButton("🏠 Home");
        homeButton.addActionListener(e -> navigateHome());
        leftPanel.add(homeButton);

        backButton = createNavButton("◀ Back");
        backButton.addActionListener(e -> goBack());
        leftPanel.add(backButton);

        navBar.add(leftPanel, BorderLayout.WEST);

        // Center - Title
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        navBar.add(titleLabel, BorderLayout.CENTER);

        // Right side - User info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        User currentUser = AuthService.getCurrentUser();
        if (currentUser != null) {
            JLabel userLabel = new JLabel("👤 " + currentUser.getUsername() +
                    (currentUser.isParent() ? " (Parent)" : " (Teen)"));
            userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            userLabel.setForeground(Color.WHITE);
            rightPanel.add(userLabel);
        }

        JButton logoutButton = createNavButton("🚪 Logout");
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);

        navBar.add(rightPanel, BorderLayout.EAST);

        return navBar;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(new Color(70, 130, 180)); // Steel blue text
        button.setBackground(Color.WHITE); // White background
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    protected void navigateHome() {
        if (AuthService.isParent()) {
            new ParentDashboardFrame().setVisible(true);
        } else {
            User currentUser = AuthService.getCurrentUser();
            new TeenDashboardFrame(currentUser).setVisible(true);
        }
        dispose();
    }

    protected void goBack() {
        navigateHome();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.logout();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    protected void setContent(JComponent content) {
        mainContentPanel.removeAll();
        mainContentPanel.add(content, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}