package com.example.carogame.security;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.carogame.entity.User;
import com.example.carogame.repository.UserRepository;
import com.example.carogame.util.JwtUtil;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Principal user = authenticateToken(token);
                if (user != null) {
                    accessor.setUser(user);
                }
            }
        }

        return message;
    }

    public Principal authenticateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            if (username != null) {
                User user = userRepository.findByUsername(username).orElse(null);

                if (user != null && jwtUtil.validateToken(token, user)) {
                    return new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        user.getAuthorities()
                    );
                }
            }
        } catch (Exception e) {
            // Log the error but don't throw exception to avoid breaking WebSocket connection
            System.err.println("WebSocket authentication error: " + e.getMessage());
        }

        return null;
    }

    // Custom Principal implementation for WebSocket authentication
    public static class WebSocketPrincipal implements Principal {
        private final String name;
        private final Authentication authentication;

        public WebSocketPrincipal(String name, Authentication authentication) {
            this.name = name;
            this.authentication = authentication;
        }

        @Override
        public String getName() {
            return name;
        }

        public Authentication getAuthentication() {
            return authentication;
        }
    }
}
