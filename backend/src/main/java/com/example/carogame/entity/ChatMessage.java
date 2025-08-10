package com.example.carogame.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.CHAT;

    @Column(name = "room_id")
    private String roomId; // For game-specific chat or global chat

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_system_message", nullable = false)
    private Boolean isSystemMessage = false;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(User sender, String content, MessageType type, String roomId) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.roomId = roomId;
        this.createdAt = LocalDateTime.now();
        this.isSystemMessage = false;
    }

    // System message constructor
    public ChatMessage(String content, MessageType type, String roomId) {
        this.content = content;
        this.type = type;
        this.roomId = roomId;
        this.createdAt = LocalDateTime.now();
        this.isSystemMessage = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsSystemMessage() {
        return isSystemMessage;
    }

    public void setIsSystemMessage(Boolean isSystemMessage) {
        this.isSystemMessage = isSystemMessage;
    }

    // Enum for message types
    public enum MessageType {
        CHAT,           // Regular chat message
        JOIN,           // User joined room
        LEAVE,          // User left room
        GAME_START,     // Game started
        GAME_END,       // Game ended
        SYSTEM          // System announcement
    }
}
