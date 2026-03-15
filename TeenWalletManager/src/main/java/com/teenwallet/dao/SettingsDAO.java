package com.teenwallet.dao;

import com.teenwallet.model.UserSettings;
import com.teenwallet.utils.DBConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsDAO {

    public static UserSettings getSettingsForUser(String username) {
        UserSettings settings = new UserSettings(username);
        String sql = "SELECT * FROM user_settings_per_teen WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                settings.setDailyLimit(rs.getDouble("daily_limit"));
                settings.setWeeklyLimit(rs.getDouble("weekly_limit"));
                settings.setCardLocked(rs.getBoolean("card_locked"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load category limits
        settings.setCategoryLimits(getAllCategoryLimitsForUser(username));

        return settings;
    }

    public static boolean saveSettingsForUser(UserSettings settings) {
        String sql = "UPDATE user_settings_per_teen SET daily_limit = ?, weekly_limit = ?, card_locked = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, settings.getDailyLimit());
            pstmt.setDouble(2, settings.getWeeklyLimit());
            pstmt.setBoolean(3, settings.isCardLocked());
            pstmt.setString(4, settings.getUsername());

            int updated = pstmt.executeUpdate();

            if (updated == 0) {
                sql = "INSERT INTO user_settings_per_teen (username, daily_limit, weekly_limit, card_locked) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertPstmt = conn.prepareStatement(sql)) {
                    insertPstmt.setString(1, settings.getUsername());
                    insertPstmt.setDouble(2, settings.getDailyLimit());
                    insertPstmt.setDouble(3, settings.getWeeklyLimit());
                    insertPstmt.setBoolean(4, settings.isCardLocked());
                    return insertPstmt.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, Double> getAllCategoryLimitsForUser(String username) {
        Map<String, Double> limits = new HashMap<>();
        String sql = "SELECT category_name, limit_amount FROM category_limits_per_teen WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                limits.put(rs.getString("category_name"), rs.getDouble("limit_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add default limits if none found
        if (limits.isEmpty()) {
            String[] categories = {"Food", "Entertainment", "Education", "Shopping", "Transport", "Others"};
            for (String cat : categories) {
                limits.put(cat, 100.0);
                createDefaultCategoryLimit(username, cat, 100.0);
            }
        }

        return limits;
    }

    private static void createDefaultCategoryLimit(String username, String category, double limit) {
        String sql = "INSERT INTO category_limits_per_teen (username, category_name, limit_amount) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, category);
            pstmt.setDouble(3, limit);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateCategoryLimitForUser(String username, String category, double limit) {
        String sql = "UPDATE category_limits_per_teen SET limit_amount = ? WHERE username = ? AND category_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, limit);
            pstmt.setString(2, username);
            pstmt.setString(3, category);

            int updated = pstmt.executeUpdate();

            if (updated == 0) {
                sql = "INSERT INTO category_limits_per_teen (username, category_name, limit_amount) VALUES (?, ?, ?)";
                try (PreparedStatement insertPstmt = conn.prepareStatement(sql)) {
                    insertPstmt.setString(1, username);
                    insertPstmt.setString(2, category);
                    insertPstmt.setDouble(3, limit);
                    return insertPstmt.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Double getCategoryLimitForUser(String username, String category) {
        String sql = "SELECT limit_amount FROM category_limits_per_teen WHERE username = ? AND category_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, category);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("limit_amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 100.0;
    }

    public static boolean isCardLockedForUser(String username) {
        String sql = "SELECT card_locked FROM user_settings_per_teen WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("card_locked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setCardLockedForUser(String username, boolean locked) {
        UserSettings settings = getSettingsForUser(username);
        settings.setCardLocked(locked);
        saveSettingsForUser(settings);
    }
}