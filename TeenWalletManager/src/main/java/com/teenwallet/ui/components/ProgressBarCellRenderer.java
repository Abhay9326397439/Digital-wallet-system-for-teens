package com.teenwallet.ui.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ProgressBarCellRenderer extends JProgressBar implements TableCellRenderer {

    public ProgressBarCellRenderer() {
        super(0, 100);
        setStringPainted(true);
        setFont(new Font("Segoe UI", Font.BOLD, 11));
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Double) {
            double progress = (Double) value;
            setValue((int) Math.round(progress));
            setString(String.format("%.1f%%", progress));

            // Set color based on progress
            if (progress >= 100) {
                setForeground(new Color(76, 175, 80)); // Green - Completed
                setString("✓ Completed");
            } else if (progress >= 80) {
                setForeground(new Color(33, 150, 243)); // Blue - Almost there
                setString("🎯 " + String.format("%.1f%%", progress));
            } else if (progress >= 50) {
                setForeground(new Color(255, 152, 0)); // Orange - Halfway
                setString(String.format("%.1f%%", progress));
            } else if (progress > 0) {
                setForeground(new Color(244, 67, 54)); // Red - Just started
                setString(String.format("%.1f%%", progress));
            } else {
                setForeground(Color.GRAY); // Gray - Not started
                setString("0%");
            }

            // Handle selection background
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
        }

        return this;
    }
}