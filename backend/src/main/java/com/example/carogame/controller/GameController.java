package com.example.carogame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.carogame.model.GameState;
import com.example.carogame.model.Move;
import com.example.carogame.model.Player;
import com.example.carogame.service.GameService;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @MessageMapping("/game/{gameId}/join")
    @SendTo("/topic/game/{gameId}")
    public GameState joinGame(@DestinationVariable String gameId, Player player) {
        return gameService.joinGame(gameId, player);
    }

    @MessageMapping("/game/{gameId}/move")
    @SendTo("/topic/game/{gameId}")
    public GameState makeMove(@DestinationVariable String gameId, Move move) {
        return gameService.makeMove(gameId, move);
    }

    @MessageMapping("/game/{gameId}/reset")
    @SendTo("/topic/game/{gameId}")
    public GameState resetGame(@DestinationVariable String gameId) {
        gameService.resetGame(gameId);
        return gameService.getGame(gameId);
    }

    @MessageMapping("/game/{gameId}/leave")
    @SendTo("/topic/game/{gameId}")
    public GameState leaveGame(@DestinationVariable String gameId, Player player) {
        return gameService.leaveGame(gameId, player.getId());
    }
}
