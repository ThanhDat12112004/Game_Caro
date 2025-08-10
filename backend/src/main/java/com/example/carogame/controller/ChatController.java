package com.example.carogame.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.carogame.entity.ChatMessage;
import com.example.carogame.entity.User;
import com.example.carogame.repository.ChatMessageRepository;
import com.example.carogame.repository.UserRepository;
import com.example.carogame.util.JwtUtil;

@Controller
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    // WebSocket endpoint for sending messages to a specific room
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId,
                                 @Payload ChatMessageRequest request,
                                 SimpMessageHeaderAccessor headerAccessor,
                                 Principal principal) {

        // Get user from JWT token in WebSocket session
        String username = principal.getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and save chat message
        ChatMessage message = new ChatMessage(sender, request.getContent(),
                                            ChatMessage.MessageType.CHAT, roomId);
        message = chatMessageRepository.save(message);

        return message;
    }

    // WebSocket endpoint for global chat
    @MessageMapping("/chat/global")
    @SendTo("/topic/chat/global")
    public ChatMessage sendGlobalMessage(@Payload ChatMessageRequest request,
                                       SimpMessageHeaderAccessor headerAccessor,
                                       Principal principal) {

        String username = principal.getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage message = new ChatMessage(sender, request.getContent(),
                                            ChatMessage.MessageType.CHAT, null);
        message = chatMessageRepository.save(message);

        return message;
    }

    // WebSocket endpoint for user joining a room
    @MessageMapping("/chat/{roomId}/join")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage joinRoom(@DestinationVariable String roomId,
                              SimpMessageHeaderAccessor headerAccessor,
                              Principal principal) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create system message for user joining
        ChatMessage joinMessage = new ChatMessage(
            user.getDisplayName() + " joined the chat",
            ChatMessage.MessageType.JOIN,
            roomId
        );
        joinMessage = chatMessageRepository.save(joinMessage);

        return joinMessage;
    }

    // WebSocket endpoint for user leaving a room
    @MessageMapping("/chat/{roomId}/leave")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage leaveRoom(@DestinationVariable String roomId,
                               SimpMessageHeaderAccessor headerAccessor,
                               Principal principal) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create system message for user leaving
        ChatMessage leaveMessage = new ChatMessage(
            user.getDisplayName() + " left the chat",
            ChatMessage.MessageType.LEAVE,
            roomId
        );
        leaveMessage = chatMessageRepository.save(leaveMessage);

        return leaveMessage;
    }

    // REST endpoint to get chat history for a room
    @GetMapping("/history/{roomId}")
    @ResponseBody
    public Page<ChatMessage> getChatHistory(@PathVariable String roomId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
    }

    // REST endpoint to get global chat history
    @GetMapping("/history/global")
    @ResponseBody
    public Page<ChatMessage> getGlobalChatHistory(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatMessageRepository.findGlobalMessages(pageable);
    }

    // REST endpoint to get recent messages for a room
    @GetMapping("/recent/{roomId}")
    @ResponseBody
    public List<ChatMessage> getRecentMessages(@PathVariable String roomId,
                                             @RequestParam(defaultValue = "20") int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return chatMessageRepository.findRecentMessagesByRoomId(roomId, pageable);
    }

    // Send system message to a room (for game events)
    public void sendSystemMessage(String roomId, String content, ChatMessage.MessageType type) {
        ChatMessage systemMessage = new ChatMessage(content, type, roomId);
        systemMessage = chatMessageRepository.save(systemMessage);

        // Send to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, systemMessage);
    }

    // DTO for chat message requests
    public static class ChatMessageRequest {
        private String content;

        public ChatMessageRequest() {}

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
