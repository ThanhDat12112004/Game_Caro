package com.example.carogame.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.carogame.service.GameService;

@Component
public class WebSocketEventListener {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // Theo dõi mapping giữa session và thông tin game/player
    private Map<String, String> sessionToGameId = new ConcurrentHashMap<>();
    private Map<String, String> sessionToPlayerId = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String gameId = sessionToGameId.get(sessionId);
        String playerId = sessionToPlayerId.get(sessionId);

        if (gameId != null && playerId != null) {
            System.out.println("Player " + playerId + " disconnected from game " + gameId);

            // Xử lý người chơi thoát game
            var updatedGame = gameService.leaveGame(gameId, playerId);

            if (updatedGame != null) {
                // Còn người chơi trong phòng, thông báo game state mới
                messagingTemplate.convertAndSend("/topic/game/" + gameId, updatedGame);

                // Thông báo riêng về việc người chơi rời phòng
                messagingTemplate.convertAndSend("/topic/game/" + gameId + "/player-left",
                    Map.of("playerId", playerId, "message", "Người chơi đã rời phòng"));
            } else {
                // Game đã bị xóa do không còn người chơi
                messagingTemplate.convertAndSend("/topic/game/" + gameId + "/deleted",
                    Map.of("message", "Phòng đã bị xóa do không còn người chơi"));
                System.out.println("Game " + gameId + " deleted due to no players");
            }

            // Cleanup session tracking
            sessionToGameId.remove(sessionId);
            sessionToPlayerId.remove(sessionId);

            System.out.println("Player " + playerId + " removed from game " + gameId);
        }
    }

    // Phương thức để đăng ký session với game và player
    public void registerSession(String sessionId, String gameId, String playerId) {
        sessionToGameId.put(sessionId, gameId);
        sessionToPlayerId.put(sessionId, playerId);
    }

    // Phương thức để hủy đăng ký session
    public void unregisterSession(String sessionId) {
        sessionToGameId.remove(sessionId);
        sessionToPlayerId.remove(sessionId);
    }
}
