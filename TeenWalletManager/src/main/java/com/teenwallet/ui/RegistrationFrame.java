package com.teenwallet.ui;

import com.teenwallet.dao.UserDAO;
import com.teenwallet.model.User;

import javax.swing.*;
import java.awt.*;

public class RegistrationFrame extends JFrame {
    private JComboBox<String> roleCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField parentUsernameField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    private UserDAO userDAO;

    public RegistrationFrame() {
        setTitle("TeenWallet Manager - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Role Selection
        JLabel roleLabel = new JLabel("Register as:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(roleLabel, gbc);

        roleCombo = new JComboBox<>(new String[]{"Parent", "Teen"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo.addActionListener(e -> onRoleChange());
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(roleCombo, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
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
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(confirmLabel, gbc);

        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(confirmPasswordField, gbc);

        // Parent Username (for teens)
        JLabel parentLabel = new JLabel("Parent Username:");
        parentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(parentLabel, gbc);

        parentUsernameField = new JTextField(15);
        parentUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        parentUsernameField.setEnabled(false);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(parentUsernameField, gbc);

        // Register Button
        registerButton = new JButton("✅ Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> performRegistration());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 8, 8, 8);
        mainPanel.add(registerButton, gbc);

        // Back to Login Button
        backButton = new JButton("◀ Back to Login");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setForeground(new Color(70, 130, 180));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(backButton, gbc);

        // Message Label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 8;
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);
    }

    private void onRoleChange() {
        String role = (String) roleCombo.getSelectedItem();
        if ("Teen".equals(role)) {
            parentUsernameField.setEnabled(true);
            parentUsernameField.setBackground(Color.WHITE);
        } else {
            parentUsernameField.setEnabled(false);
            parentUsernameField.setBackground(new Color(240, 240, 240));
            parentUsernameField.setText("");
        }
    }

    private void performRegistration() {
        String role = (String) roleCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String parentUsername = parentUsernameField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill all fields", true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match", true);
            return;
        }

        if (password.length() < 3) {
            showMessage("Password must be at least 3 characters", true);
            return;
        }

        if ("Teen".equals(role) && parentUsername.isEmpty()) {
            showMessage("Please enter parent username", true);
            return;
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            showMessage("Username already exists", true);
            return;
        }

        // For teen, check if parent exists
        if ("Teen".equals(role)) {
            User parent = userDAO.getUserByUsername(parentUsername);
            if (parent == null || !parent.isParent()) {
                showMessage("Parent username not found or invalid", true);
                return;
            }
        }

        // Create user
        User newUser = new User(0, username, password, role.toUpperCase(),
                "Teen".equals(role) ? parentUsername : null, true);

        if (userDAO.addUser(newUser)) {
            showMessage("Registration successful! Redirecting to login...", false);

            Timer timer = new Timer(2000, e -> {
                new LoginFrame().setVisible(true);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            showMessage("Registration failed. Please try again.", true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : new Color(0, 150, 0));
    }
}