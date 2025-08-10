package com.example.carogame.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.carogame.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Find messages by room ID with pagination
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(String roomId, Pageable pageable);

    // Find recent messages by room ID (last N messages)
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessagesByRoomId(@Param("roomId") String roomId, Pageable pageable);

    // Find global chat messages (roomId is null)
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId IS NULL ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findGlobalMessages(Pageable pageable);

    // Find messages by sender
    Page<ChatMessage> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);

    // Find messages created after a specific time
    List<ChatMessage> findByRoomIdAndCreatedAtAfterOrderByCreatedAtAsc(String roomId, LocalDateTime after);

    // Count messages in a room
    long countByRoomId(String roomId);

    // Delete old messages (for cleanup)
    void deleteByCreatedAtBefore(LocalDateTime before);

    // Find system messages
    List<ChatMessage> findByIsSystemMessageTrueAndRoomIdOrderByCreatedAtDesc(String roomId);
}
