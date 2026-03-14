package com.teenwallet.service;

import com.teenwallet.model.User;

public class AuthService {
    private static User currentUser = null;

    // Hardcoded users for demo
    private static final User PARENT = new User("parent", "123", User.UserRole.PARENT);
    private static final User TEEN = new User("teen", "123", User.UserRole.TEEN);

    public static User login(String username, String password) {
        if (PARENT.getUsername().equals(username) && PARENT.getPassword().equals(password)) {
            currentUser = PARENT;
            return PARENT;
        } else if (TEEN.getUsername().equals(username) && TEEN.getPassword().equals(password)) {
            currentUser = TEEN;
            return TEEN;
        }
        return null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isParent() {
        return currentUser != null && currentUser.getRole() == User.UserRole.PARENT;
    }

    public static boolean isTeen() {
        return currentUser != null && currentUser.getRole() == User.UserRole.TEEN;
    }
}