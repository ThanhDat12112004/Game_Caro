package com.example.carogame.dto;

import com.example.carogame.model.BoardType;

public class CreateGameRequest {
    private String gameId;
    private BoardType boardType;

    public CreateGameRequest() {
        this.boardType = BoardType.STANDARD;
    }

    public CreateGameRequest(String gameId, BoardType boardType) {
        this.gameId = gameId;
        this.boardType = boardType;
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
    }
}
