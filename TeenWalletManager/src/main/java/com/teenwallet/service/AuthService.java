package com.teenwallet.service;

import com.teenwallet.model.User;
import com.teenwallet.dao.UserDAO;
import com.teenwallet.dao.TransactionDAO;

public class AuthService {
    private static User currentUser = null;
    private static UserDAO userDAO = new UserDAO();

    public static User login(String username, String password, String role) {
        System.out.println("Login attempt - Username: " + username + ", Password: " + password + ", Role: " + role);

        // Get user from database
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            System.out.println("User not found: " + username);
            return null;
        }

        System.out.println("User found - DB Password: " + user.getPassword() + ", Role: " + user.getRole());

        // Check password (case-sensitive)
        if (!user.getPassword().equals(password)) {
            System.out.println("Password mismatch");
            return null;
        }

        // Check role match
        String expectedRole = "Parent".equals(role) ? "PARENT" : "TEEN";
        if (!user.getRole().equals(expectedRole)) {
            System.out.println("Role mismatch - Expected: " + expectedRole + ", Found: " + user.getRole());
            return null;
        }

        // Load teen's wallet balance if applicable
        if (user.isTeen()) {
            double balance = TransactionDAO.getCurrentBalanceForUser(username);
            user.setWalletBalance(balance);
        }

        currentUser = user;
        System.out.println("Login successful for: " + username);
        return user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isParent() {
        return currentUser != null && "PARENT".equals(currentUser.getRole());
    }

    public static boolean isTeen() {
        return currentUser != null && "TEEN".equals(currentUser.getRole());
    }

    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
}