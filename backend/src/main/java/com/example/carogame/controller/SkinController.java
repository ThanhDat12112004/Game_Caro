package com.example.carogame.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carogame.entity.BoardSkin;
import com.example.carogame.entity.PieceSkin;
import com.example.carogame.entity.User;
import com.example.carogame.entity.UserSkin;
import com.example.carogame.repository.UserRepository;
import com.example.carogame.service.SkinService;
import com.example.carogame.util.JwtUtil;

@RestController
@RequestMapping("/api/skins")
@CrossOrigin(origins = "*")
public class SkinController {

    @Autowired
    private SkinService skinService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/board")
    public ResponseEntity<List<BoardSkin>> getAllBoardSkins() {
        List<BoardSkin> skins = skinService.getAllActiveBoardSkins();
        return ResponseEntity.ok(skins);
    }

    @GetMapping("/piece")
    public ResponseEntity<List<PieceSkin>> getAllPieceSkins() {
        List<PieceSkin> skins = skinService.getAllActivePieceSkins();
        return ResponseEntity.ok(skins);
    }

    @GetMapping("/user/board")
    public ResponseEntity<List<UserSkin>> getUserBoardSkins(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<UserSkin> userSkins = skinService.getUserBoardSkins(userOpt.get());
            return ResponseEntity.ok(userSkins);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/user/piece")
    public ResponseEntity<List<UserSkin>> getUserPieceSkins(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<UserSkin> userSkins = skinService.getUserPieceSkins(userOpt.get());
            return ResponseEntity.ok(userSkins);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/purchase/board/{skinName}")
    public ResponseEntity<Map<String, Object>> purchaseBoardSkin(
            @PathVariable String skinName,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(401).body(error);
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User not found");
                return ResponseEntity.status(404).body(error);
            }

            boolean success = skinService.purchaseBoardSkin(userOpt.get().getId(), skinName);
            Map<String, Object> response = new HashMap<>();

            if (success) {
                response.put("success", true);
                response.put("message", "Board skin purchased successfully!");

                // Return updated user balance
                User updatedUser = userRepository.findById(userOpt.get().getId()).orElse(null);
                if (updatedUser != null) {
                    response.put("newBalance", updatedUser.getBalance());
                }
            } else {
                response.put("success", false);
                response.put("message", "Failed to purchase board skin. Check your balance or if you already own this skin.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid token");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/purchase/piece/{skinName}")
    public ResponseEntity<Map<String, Object>> purchasePieceSkin(
            @PathVariable String skinName,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(401).body(error);
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User not found");
                return ResponseEntity.status(404).body(error);
            }

            boolean success = skinService.purchasePieceSkin(userOpt.get().getId(), skinName);
            Map<String, Object> response = new HashMap<>();

            if (success) {
                response.put("success", true);
                response.put("message", "Piece skin purchased successfully!");

                // Return updated user balance
                User updatedUser = userRepository.findById(userOpt.get().getId()).orElse(null);
                if (updatedUser != null) {
                    response.put("newBalance", updatedUser.getBalance());
                }
            } else {
                response.put("success", false);
                response.put("message", "Failed to purchase piece skin. Check your balance or if you already own this skin.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid token");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/select/board/{skinName}")
    public ResponseEntity<Map<String, Object>> selectBoardSkin(
            @PathVariable String skinName,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(401).body(error);
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User not found");
                return ResponseEntity.status(404).body(error);
            }

            boolean success = skinService.selectBoardSkin(userOpt.get().getId(), skinName);
            Map<String, Object> response = new HashMap<>();

            if (success) {
                response.put("success", true);
                response.put("message", "Board skin selected successfully!");
            } else {
                response.put("success", false);
                response.put("message", "Failed to select board skin. You may not own this skin.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid token");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/select/piece/{skinName}")
    public ResponseEntity<Map<String, Object>> selectPieceSkin(
            @PathVariable String skinName,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(401).body(error);
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User not found");
                return ResponseEntity.status(404).body(error);
            }

            boolean success = skinService.selectPieceSkin(userOpt.get().getId(), skinName);
            Map<String, Object> response = new HashMap<>();

            if (success) {
                response.put("success", true);
                response.put("message", "Piece skin selected successfully!");
            } else {
                response.put("success", false);
                response.put("message", "Failed to select piece skin. You may not own this skin.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid token");
            return ResponseEntity.status(401).body(error);
        }
    }

    @GetMapping("/user/balance")
    public ResponseEntity<Map<String, Object>> getUserBalance(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Authorization header missing or invalid");
            return ResponseEntity.status(401).body(error);
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(404).body(error);
            }

            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("balance", user.getBalance());
            response.put("totalWins", user.getTotalWins());
            response.put("totalGames", user.getTotalGames());
            response.put("winRate", user.getWinRate());
            response.put("selectedBoardSkin", user.getSelectedBoardSkin());
            response.put("selectedPieceSkin", user.getSelectedPieceSkin());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid token");
            return ResponseEntity.status(401).body(error);
        }
    }
}
