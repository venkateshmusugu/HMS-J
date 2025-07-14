package com.sanjittech.hms.dto;

import com.sanjittech.hms.config.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class RegisterRequest {
    private String username;
    private String password;
    private  String email;

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    private String role; // RECEPTIONIST, DOCTOR, etc.

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return  email;
    }
}
