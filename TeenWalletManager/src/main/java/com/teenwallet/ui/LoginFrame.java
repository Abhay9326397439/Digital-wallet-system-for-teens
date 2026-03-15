package com.teenwallet.ui;

import com.teenwallet.service.AuthService;
import com.teenwallet.model.User;
import com.teenwallet.dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JComboBox<String> roleCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private UserDAO userDAO;

    public LoginFrame() {
        setTitle("TeenWallet Manager - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(70, 130, 180);
                Color color2 = new Color(25, 25, 112);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo/Title
        JLabel titleLabel = new JLabel("💰 TeenWallet Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Parental Control & Allowance System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(255, 215, 0));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // Role Selection
        JLabel roleLabel = new JLabel("Login as:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(roleLabel, gbc);

        roleCombo = new JComboBox<>(new String[]{"Parent", "Teen"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(roleCombo, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        loginButton = new JButton("🔓 Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 130, 180)); // Steel blue
        loginButton.setForeground(Color.BLACK); // White text
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());
        buttonPanel.add(loginButton);

        registerButton = new JButton("📝 Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setBackground(new Color(46, 125, 50)); // Green
        registerButton.setForeground(Color.BLACK); // White text
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> openRegistration());
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(Color.WHITE);
        gbc.gridy = 6;
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        // Enter key listener
        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String role = (String) roleCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password", true);
            return;
        }

        User user = AuthService.login(username, password, role);

        if (user != null) {
            showMessage("Login successful! Redirecting...", false);

            if ("Parent".equals(role)) {
                new ParentDashboardFrame().setVisible(true);
            } else {
                new TeenDashboardFrame(user).setVisible(true);
            }

            dispose();
        } else {
            showMessage("Invalid username or password!", true);
        }
    }

    private void openRegistration() {
        new RegistrationFrame().setVisible(true);
        dispose();
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? new Color(255, 99, 71) : new Color(144, 238, 144));
    }
}