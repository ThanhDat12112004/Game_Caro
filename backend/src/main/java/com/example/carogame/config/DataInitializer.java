package com.example.carogame.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.carogame.entity.BoardSkin;
import com.example.carogame.entity.BoardType;
import com.example.carogame.entity.PieceSkin;
import com.example.carogame.entity.Role;
import com.example.carogame.entity.User;
import com.example.carogame.repository.BoardSkinRepository;
import com.example.carogame.repository.BoardTypeRepository;
import com.example.carogame.repository.PieceSkinRepository;
import com.example.carogame.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private BoardSkinRepository boardSkinRepository;

    @Autowired
    private PieceSkinRepository pieceSkinRepository;

    @Autowired
    private BoardTypeRepository boardTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() {
        initializeBoardTypes();
        initializeBoardSkins();
        initializePieceSkins();
        updateExistingUsersWithRoles();
        initializeDefaultUser();
    }

    private void updateExistingUsersWithRoles() {
        // Update existing users who don't have roles assigned
        List<User> usersWithoutRoles = userRepository.findAll().stream()
                .filter(user -> user.getRole() == null)
                .collect(java.util.stream.Collectors.toList());

        for (User user : usersWithoutRoles) {
            user.setRole(Role.USER); // Set default role as USER
            userRepository.save(user);
        }
    }

    private void initializeBoardTypes() {
        // Classic 15x15 Board (Default)
        if (!boardTypeRepository.findByName("classic_15x15").isPresent()) {
            BoardType classic15 = new BoardType();
            classic15.setName("classic_15x15");
            classic15.setDisplayName("Classic 15x15");
            classic15.setDescription("Traditional Caro game on 15x15 board with 5 in a row to win");
            classic15.setBoardSize(15);
            classic15.setWinCondition(5);
            classic15.setIsActive(true);
            classic15.setIsDefault(true);
            boardTypeRepository.save(classic15);
        }

        // Small 10x10 Board
        if (!boardTypeRepository.findByName("small_10x10").isPresent()) {
            BoardType small10 = new BoardType();
            small10.setName("small_10x10");
            small10.setDisplayName("Small 10x10");
            small10.setDescription("Quick games on smaller 10x10 board with 5 in a row to win");
            small10.setBoardSize(10);
            small10.setWinCondition(5);
            small10.setIsActive(true);
            small10.setIsDefault(false);
            boardTypeRepository.save(small10);
        }

        // Large 20x20 Board
        if (!boardTypeRepository.findByName("large_20x20").isPresent()) {
            BoardType large20 = new BoardType();
            large20.setName("large_20x20");
            large20.setDisplayName("Large 20x20");
            large20.setDescription("Extended gameplay on large 20x20 board with 5 in a row to win");
            large20.setBoardSize(20);
            large20.setWinCondition(5);
            large20.setIsActive(true);
            large20.setIsDefault(false);
            boardTypeRepository.save(large20);
        }

        // Mini 8x8 Board (4 in a row)
        if (!boardTypeRepository.findByName("mini_8x8").isPresent()) {
            BoardType mini8 = new BoardType();
            mini8.setName("mini_8x8");
            mini8.setDisplayName("Mini 8x8");
            mini8.setDescription("Fast-paced games on mini 8x8 board with 4 in a row to win");
            mini8.setBoardSize(8);
            mini8.setWinCondition(4);
            mini8.setIsActive(true);
            mini8.setIsDefault(false);
            boardTypeRepository.save(mini8);
        }

        // Giant 25x25 Board
        if (!boardTypeRepository.findByName("giant_25x25").isPresent()) {
            BoardType giant25 = new BoardType();
            giant25.setName("giant_25x25");
            giant25.setDisplayName("Giant 25x25");
            giant25.setDescription("Epic battles on giant 25x25 board with 5 in a row to win");
            giant25.setBoardSize(25);
            giant25.setWinCondition(5);
            giant25.setIsActive(true);
            giant25.setIsDefault(false);
            boardTypeRepository.save(giant25);
        }
    }

    private void initializeBoardSkins() {
        // Classic Board Skin (Free)
        if (!boardSkinRepository.findByName("classic").isPresent()) {
            BoardSkin classic = new BoardSkin();
            classic.setName("classic");
            classic.setDisplayName("Classic Board");
            classic.setDescription("The traditional Caro game board with clean lines and simple design");
            classic.setPrice(0L);
            classic.setCssClass("board-classic");
            classic.setBackgroundColor("#f8f9fa");
            classic.setBorderColor("#dee2e6");
            classic.setCellColor("#ffffff");
            classic.setHoverColor("#e9ecef");
            classic.setIsPremium(false);
            classic.setIsActive(true);
            boardSkinRepository.save(classic);
        }

        // Dark Theme Board Skin
        if (!boardSkinRepository.findByName("dark").isPresent()) {
            BoardSkin dark = new BoardSkin();
            dark.setName("dark");
            dark.setDisplayName("Dark Theme");
            dark.setDescription("Sleek dark theme perfect for night gaming sessions");
            dark.setPrice(500L);
            dark.setCssClass("board-dark");
            dark.setBackgroundColor("#212529");
            dark.setBorderColor("#495057");
            dark.setCellColor("#343a40");
            dark.setHoverColor("#495057");
            dark.setIsPremium(false);
            dark.setIsActive(true);
            boardSkinRepository.save(dark);
        }

        // Ocean Theme Board Skin
        if (!boardSkinRepository.findByName("ocean").isPresent()) {
            BoardSkin ocean = new BoardSkin();
            ocean.setName("ocean");
            ocean.setDisplayName("Ocean Blue");
            ocean.setDescription("Calming ocean-inspired theme with blue gradients");
            ocean.setPrice(750L);
            ocean.setCssClass("board-ocean");
            ocean.setBackgroundColor("#e3f2fd");
            ocean.setBorderColor("#1976d2");
            ocean.setCellColor("#bbdefb");
            ocean.setHoverColor("#90caf9");
            ocean.setIsPremium(false);
            ocean.setIsActive(true);
            boardSkinRepository.save(ocean);
        }

        // Neon Theme Board Skin (Premium)
        if (!boardSkinRepository.findByName("neon").isPresent()) {
            BoardSkin neon = new BoardSkin();
            neon.setName("neon");
            neon.setDisplayName("Neon Glow");
            neon.setDescription("Futuristic neon theme with glowing effects");
            neon.setPrice(1500L);
            neon.setCssClass("board-neon");
            neon.setBackgroundColor("#0a0a0a");
            neon.setBorderColor("#00ff41");
            neon.setCellColor("#1a1a1a");
            neon.setHoverColor("#003d16");
            neon.setIsPremium(true);
            neon.setIsActive(true);
            boardSkinRepository.save(neon);
        }
    }

    private void initializePieceSkins() {
        // Classic Piece Skin (Free)
        if (!pieceSkinRepository.findByName("classic").isPresent()) {
            PieceSkin classic = new PieceSkin();
            classic.setName("classic");
            classic.setDisplayName("Classic X & O");
            classic.setDescription("Traditional X and O symbols");
            classic.setPrice(0L);
            classic.setXSymbol("X");
            classic.setOSymbol("O");
            classic.setXColor("#dc3545");
            classic.setOColor("#007bff");
            classic.setXBackgroundColor("#ffffff");
            classic.setOBackgroundColor("#ffffff");
            classic.setCssClass("piece-classic");
            classic.setAnimationClass("");
            classic.setIsPremium(false);
            classic.setIsActive(true);
            pieceSkinRepository.save(classic);
        }

        // Emoji Piece Skin
        if (!pieceSkinRepository.findByName("emoji").isPresent()) {
            PieceSkin emoji = new PieceSkin();
            emoji.setName("emoji");
            emoji.setDisplayName("Emoji Fun");
            emoji.setDescription("Fun emoji symbols for a playful experience");
            emoji.setPrice(300L);
            emoji.setXSymbol("😊");
            emoji.setOSymbol("😎");
            emoji.setXColor("#ffc107");
            emoji.setOColor("#28a745");
            emoji.setXBackgroundColor("#ffffff");
            emoji.setOBackgroundColor("#ffffff");
            emoji.setCssClass("piece-emoji");
            emoji.setAnimationClass("");
            emoji.setIsPremium(false);
            emoji.setIsActive(true);
            pieceSkinRepository.save(emoji);
        }

        // Symbols Piece Skin
        if (!pieceSkinRepository.findByName("symbols").isPresent()) {
            PieceSkin symbols = new PieceSkin();
            symbols.setName("symbols");
            symbols.setDisplayName("Special Symbols");
            symbols.setDescription("Unique symbols for a different gaming experience");
            symbols.setPrice(600L);
            symbols.setXSymbol("★");
            symbols.setOSymbol("♦");
            symbols.setXColor("#ff6b35");
            symbols.setOColor("#6f42c1");
            symbols.setXBackgroundColor("#ffffff");
            symbols.setOBackgroundColor("#ffffff");
            symbols.setCssClass("piece-symbols");
            symbols.setAnimationClass("");
            symbols.setIsPremium(false);
            symbols.setIsActive(true);
            pieceSkinRepository.save(symbols);
        }

        // Premium Animated Piece Skin
        if (!pieceSkinRepository.findByName("animated").isPresent()) {
            PieceSkin animated = new PieceSkin();
            animated.setName("animated");
            animated.setDisplayName("Animated Pieces");
            animated.setDescription("Animated pieces with special effects");
            animated.setPrice(1200L);
            animated.setXSymbol("⚡");
            animated.setOSymbol("🔥");
            animated.setXColor("#ffd700");
            animated.setOColor("#ff4500");
            animated.setXBackgroundColor("#ffffcc");
            animated.setOBackgroundColor("#ffe5cc");
            animated.setCssClass("piece-animated");
            animated.setAnimationClass("pulse-animation");
            animated.setIsPremium(true);
            animated.setIsActive(true);
            pieceSkinRepository.save(animated);
        }
    }

    private void initializeDefaultUser() {
        // Create a default test user if not exists
        if (!userRepository.findByUsername("testuser").isPresent()) {
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setDisplayName("Test User");
            testUser.setBalance(5000L); // Give test user more coins to test purchasing
            testUser.setSelectedBoardSkin("classic");
            testUser.setSelectedPieceSkin("classic");
            testUser.setRole(Role.USER);
            testUser.setEnabled(true);
            userRepository.save(testUser);
        }

        // Create an admin user if not exists
        if (!userRepository.findByUsername("admin").isPresent()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setDisplayName("Administrator");
            adminUser.setBalance(10000L); // Give admin user lots of coins
            adminUser.setSelectedBoardSkin("classic");
            adminUser.setSelectedPieceSkin("classic");
            adminUser.setRole(Role.ADMIN);
            adminUser.setEnabled(true);
            userRepository.save(adminUser);
        }
    }
}
