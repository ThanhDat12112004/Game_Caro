package com.example.carogame.model;

public class Move {
    private int row;
    private int col;
    private String playerId;
    private String symbol;

    public Move() {}

    public Move(int row, int col, String playerId, String symbol) {
        this.row = row;
        this.col = col;
        this.playerId = playerId;
        this.symbol = symbol;
    }

    // Getters and Setters
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
