package com.example.carogame.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carogame.dto.CreateGameRequest;
import com.example.carogame.model.BoardType;
import com.example.carogame.model.GameState;
import com.example.carogame.service.GameService;

@RestController
@RequestMapping("/api")
public class GameRestController {

    @Autowired
    private GameService gameService;

    @GetMapping("/board-types-enum")
    public BoardType[] getBoardTypesEnum() {
        return BoardType.values();
    }

    @PostMapping("/game/create")
    public GameState createGame(@RequestBody CreateGameRequest request) {
        return gameService.createGame(request.getGameId(), request.getBoardType());
    }

    @GetMapping("/game/{gameId}")
    public GameState getGame(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }

    @GetMapping("/games")
    public Map<String, Object> getAllGames() {
        return gameService.getAllGamesInfo();
    }
}
