package com.teenwallet;

import com.teenwallet.ui.LoginFrame;
import com.teenwallet.utils.DBConnection;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Test database connection
        DBConnection.testConnection();

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Apply custom color scheme
        applyCustomTheme();

        // Launch login screen
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }

    private static void applyCustomTheme() {
        // Modern flat color scheme
        UIManager.put("Panel.background", new Color(240, 248, 255));
        UIManager.put("Button.background", new Color(70, 130, 180));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("ProgressBar.foreground", new Color(50, 205, 50));
        UIManager.put("ProgressBar.background", new Color(220, 220, 220));
    }
}