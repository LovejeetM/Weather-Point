package com.weatherpoint.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.weatherpoint.model.User;

public class UserService {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    static {
        users.put("admin", new User("admin", "hello", "admin@weatherpoint.com"));

        users.put("lavi", new User("lavi", "hello", "test@weatherpoint.com"));
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public void registerUser(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (users.containsKey(username.trim())) {
            throw new IllegalArgumentException("Username already exists: " + username.trim());
        }

        users.put(username.trim(), new User(username, password, email));
    }
}