package com.midterm.myposapplication;

public class User {
    private String userId;
    private String username;
    private String password;
    private UserRole role;
    private String fullName;
    private long createdAt;
    private boolean isActive;
    
    public User() {
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }
    
    public User(String username, String password, UserRole role, String fullName) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
    
    public boolean isEmployee() {
        return role == UserRole.EMPLOYEE;
    }
}