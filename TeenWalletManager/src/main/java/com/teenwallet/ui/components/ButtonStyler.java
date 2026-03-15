package com.teenwallet.ui.components;

import javax.swing.*;
import java.awt.*;

public class ButtonStyler {

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(173, 216, 230)); // Light blue
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSuccessButton(JButton button) {
        button.setBackground(new Color(144, 238, 144)); // Light green
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80), 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleWarningButton(JButton button) {
        button.setBackground(new Color(255, 218, 185)); // Peach
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 152, 0), 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleActionButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleNavButton(JButton button) {
        button.setBackground(new Color(25, 25, 112)); // Dark blue
        button.setForeground(Color.WHITE); // Keep white for nav
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}