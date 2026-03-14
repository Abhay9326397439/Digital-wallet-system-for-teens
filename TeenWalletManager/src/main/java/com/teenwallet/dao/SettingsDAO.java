package com.teenwallet.dao;

import com.teenwallet.model.UserSettings;
import com.teenwallet.utils.DBConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsDAO {

    public UserSettings getSettings() {
        String sql = "SELECT * FROM user_settings LIMIT 1";
        UserSettings settings = new UserSettings();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                settings.setDailyLimit(rs.getDouble("daily_limit"));
                settings.setWeeklyLimit(rs.getDouble("weekly_limit"));
                settings.setCardLocked(rs.getBoolean("card_locked"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load category limits
        settings.setCategoryLimits(getAllCategoryLimits());

        return settings;
    }

    public boolean saveSettings(UserSettings settings) {
        String sql = "UPDATE user_settings SET daily_limit = ?, weekly_limit = ?, card_locked = ? WHERE id = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, settings.getDailyLimit());
            pstmt.setDouble(2, settings.getWeeklyLimit());
            pstmt.setBoolean(3, settings.isCardLocked());

            int updated = pstmt.executeUpdate();

            if (updated == 0) {
                // Insert if no record exists
                sql = "INSERT INTO user_settings (id, daily_limit, weekly_limit, card_locked) VALUES (1, ?, ?, ?)";
                try (PreparedStatement insertPstmt = conn.prepareStatement(sql)) {
                    insertPstmt.setDouble(1, settings.getDailyLimit());
                    insertPstmt.setDouble(2, settings.getWeeklyLimit());
                    insertPstmt.setBoolean(3, settings.isCardLocked());
                    return insertPstmt.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Double> getAllCategoryLimits() {
        Map<String, Double> limits = new HashMap<>();
        String sql = "SELECT category_name, limit_amount FROM category_limits";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                limits.put(rs.getString("category_name"), rs.getDouble("limit_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add default limits if none found
        if (limits.isEmpty()) {
            limits.put("Food", 100.0);
            limits.put("Entertainment", 150.0);
            limits.put("Education", 200.0);
            limits.put("Shopping", 200.0);
            limits.put("Transport", 100.0);
            limits.put("Others", 100.0);
        }

        return limits;
    }

    public boolean updateCategoryLimit(String category, double limit) {
        String sql = "UPDATE category_limits SET limit_amount = ? WHERE category_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, limit);
            pstmt.setString(2, category);

            int updated = pstmt.executeUpdate();

            if (updated == 0) {
                sql = "INSERT INTO category_limits (category_name, limit_amount) VALUES (?, ?)";
                try (PreparedStatement insertPstmt = conn.prepareStatement(sql)) {
                    insertPstmt.setString(1, category);
                    insertPstmt.setDouble(2, limit);
                    return insertPstmt.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Double getCategoryLimit(String category) {
        String sql = "SELECT limit_amount FROM category_limits WHERE category_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("limit_amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 100.0; // Default limit
    }
}