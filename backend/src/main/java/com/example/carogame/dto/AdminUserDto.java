package com.example.carogame.dto;

public class AdminUserDto {
    private String displayName;
    private String email;
    private Long balance;
    private Boolean enabled;
    private String role;

    // Constructors
    public AdminUserDto() {}

    public AdminUserDto(String displayName, String email, Long balance, Boolean enabled, String role) {
        this.displayName = displayName;
        this.email = email;
        this.balance = balance;
        this.enabled = enabled;
        this.role = role;
    }

    // Getters and setters
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
