package com.teenwallet.dao;

import com.teenwallet.model.SavingsGoal;
import com.teenwallet.utils.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SavingsGoalDAO {

    public boolean addGoal(SavingsGoal goal) {
        String sql = "INSERT INTO savings_goals (name, target_amount, current_amount, target_date, completed) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, goal.getName());
            pstmt.setDouble(2, goal.getTargetAmount());
            pstmt.setDouble(3, goal.getCurrentAmount());
            pstmt.setDate(4, Date.valueOf(goal.getTargetDate()));
            pstmt.setBoolean(5, goal.isCompleted());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    goal.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SavingsGoal> getAllGoals() {
        List<SavingsGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM savings_goals ORDER BY target_date";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                goals.add(extractGoalFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

    public SavingsGoal getGoalById(int id) {
        String sql = "SELECT * FROM savings_goals WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGoalFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateGoal(SavingsGoal goal) {
        String sql = "UPDATE savings_goals SET name = ?, target_amount = ?, current_amount = ?, target_date = ?, completed = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, goal.getName());
            pstmt.setDouble(2, goal.getTargetAmount());
            pstmt.setDouble(3, goal.getCurrentAmount());
            pstmt.setDate(4, Date.valueOf(goal.getTargetDate()));
            pstmt.setBoolean(5, goal.isCompleted());
            pstmt.setInt(6, goal.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGoal(int id) {
        String sql = "DELETE FROM savings_goals WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalSaved() {
        String sql = "SELECT COALESCE(SUM(current_amount), 0) as total FROM savings_goals";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getCompletedGoalsCount() {
        String sql = "SELECT COUNT(*) as count FROM savings_goals WHERE completed = true";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private SavingsGoal extractGoalFromResultSet(ResultSet rs) throws SQLException {
        return new SavingsGoal(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("target_amount"),
                rs.getDouble("current_amount"),
                rs.getDate("target_date").toLocalDate(),
                rs.getBoolean("completed")
        );
    }
}