package com.example.carogame.model;

public class Player {
    private String id;
    private String name;
    private String symbol; // "X" or "O"
    private String userId; // ID của user đã đăng nhập

    public Player() {}

    public Player(String id, String name, String symbol) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
    }

    public Player(String id, String name, String symbol, String userId) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
