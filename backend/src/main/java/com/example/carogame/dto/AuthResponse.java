package com.example.carogame.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String displayName;
    private String email;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String displayName, String email) {
        this.token = token;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
}
