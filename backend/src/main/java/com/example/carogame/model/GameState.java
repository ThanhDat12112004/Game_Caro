package com.example.carogame.model;

import java.util.List;

public class GameState {
    private String[][] board;
    private String currentPlayer;
    private boolean gameOver;
    private String winner;
    private List<Player> players;
    private String gameId;
    private BoardType boardType;

    public GameState() {
        this.boardType = BoardType.STANDARD; // Default 15x15
        this.board = new String[boardType.getSize()][boardType.getSize()];
        this.gameOver = false;
    }

    public GameState(String gameId) {
        this();
        this.gameId = gameId;
    }

    public GameState(String gameId, BoardType boardType) {
        this.gameId = gameId;
        this.boardType = boardType;
        this.board = new String[boardType.getSize()][boardType.getSize()];
        this.gameOver = false;
    }

    // Getters and Setters
    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public void setBoardType(BoardType boardType) {
        this.boardType = boardType;
        // Reinitialize board when type changes
        this.board = new String[boardType.getSize()][boardType.getSize()];
    }

    public int getBoardSize() {
        return boardType.getSize();
    }
}
