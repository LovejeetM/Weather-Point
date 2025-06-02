package com.weatherpoint.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String email;
    private String preferredLocation;


    public User(String username, String password, String email) {
        validateInput(username, "Username");
        validateInput(password, "Password");
        validateInput(email, "Email");
        
         if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        this.username = username.trim();
        this.password = password; 
        this.email = email.trim();
    }

    private void validateInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPreferredLocation() { return preferredLocation; }

    public void setPassword(String password) {
        validateInput(password, "Password");
        this.password = password;
    }

    public void setEmail(String email) {
        validateInput(email, "Email");
         if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim();
    }

    public void setPreferredLocation(String location) {
        this.preferredLocation = location != null ? location.trim() : null;
    }
}