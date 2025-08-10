package com.example.carogame.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carogame.dto.AdminUserDto;
import com.example.carogame.entity.BoardSkin;
import com.example.carogame.entity.BoardType;
import com.example.carogame.entity.PieceSkin;
import com.example.carogame.entity.Role;
import com.example.carogame.entity.User;
import com.example.carogame.repository.BoardSkinRepository;
import com.example.carogame.repository.BoardTypeRepository;
import com.example.carogame.repository.PieceSkinRepository;
import com.example.carogame.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardTypeRepository boardTypeRepository;

    @Autowired
    private BoardSkinRepository boardSkinRepository;

    @Autowired
    private PieceSkinRepository pieceSkinRepository;

    // ================== USER MANAGEMENT ==================

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody AdminUserDto userDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setDisplayName(userDto.getDisplayName());
        user.setEmail(userDto.getEmail());
        user.setBalance(userDto.getBalance());
        user.setEnabled(userDto.getEnabled());

        if (userDto.getRole() != null) {
            user.setRole(Role.valueOf(userDto.getRole()));
        }

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setEnabled(!user.getEnabled());
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // ================== BOARD TYPE MANAGEMENT ==================

    @GetMapping("/board-types")
    public ResponseEntity<List<BoardType>> getAllBoardTypes() {
        List<BoardType> boardTypes = boardTypeRepository.findAll();
        return ResponseEntity.ok(boardTypes);
    }

    @PostMapping("/board-types")
    public ResponseEntity<BoardType> createBoardType(@RequestBody BoardType boardType) {
        BoardType savedBoardType = boardTypeRepository.save(boardType);
        return ResponseEntity.ok(savedBoardType);
    }

    @PutMapping("/board-types/{id}")
    public ResponseEntity<BoardType> updateBoardType(@PathVariable Long id, @RequestBody BoardType boardType) {
        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if (!optionalBoardType.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        BoardType existingBoardType = optionalBoardType.get();
        existingBoardType.setName(boardType.getName());
        existingBoardType.setDisplayName(boardType.getDisplayName());
        existingBoardType.setDescription(boardType.getDescription());
        existingBoardType.setBoardSize(boardType.getBoardSize());
        existingBoardType.setWinCondition(boardType.getWinCondition());
        existingBoardType.setIsActive(boardType.getIsActive());
        existingBoardType.setIsDefault(boardType.getIsDefault());

        BoardType updatedBoardType = boardTypeRepository.save(existingBoardType);
        return ResponseEntity.ok(updatedBoardType);
    }

    @DeleteMapping("/board-types/{id}")
    public ResponseEntity<Void> deleteBoardType(@PathVariable Long id) {
        if (!boardTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        boardTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================== BOARD SKIN MANAGEMENT ==================

    @GetMapping("/board-skins")
    public ResponseEntity<List<BoardSkin>> getAllBoardSkins() {
        List<BoardSkin> boardSkins = boardSkinRepository.findAll();
        return ResponseEntity.ok(boardSkins);
    }

    @PostMapping("/board-skins")
    public ResponseEntity<BoardSkin> createBoardSkin(@RequestBody BoardSkin boardSkin) {
        BoardSkin savedBoardSkin = boardSkinRepository.save(boardSkin);
        return ResponseEntity.ok(savedBoardSkin);
    }

    @PutMapping("/board-skins/{id}")
    public ResponseEntity<BoardSkin> updateBoardSkin(@PathVariable Long id, @RequestBody BoardSkin boardSkin) {
        Optional<BoardSkin> optionalBoardSkin = boardSkinRepository.findById(id);
        if (!optionalBoardSkin.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        BoardSkin existingBoardSkin = optionalBoardSkin.get();
        existingBoardSkin.setName(boardSkin.getName());
        existingBoardSkin.setDisplayName(boardSkin.getDisplayName());
        existingBoardSkin.setDescription(boardSkin.getDescription());
        existingBoardSkin.setPrice(boardSkin.getPrice());
        existingBoardSkin.setCssClass(boardSkin.getCssClass());
        existingBoardSkin.setBackgroundColor(boardSkin.getBackgroundColor());
        existingBoardSkin.setBorderColor(boardSkin.getBorderColor());
        existingBoardSkin.setCellColor(boardSkin.getCellColor());
        existingBoardSkin.setHoverColor(boardSkin.getHoverColor());
        existingBoardSkin.setIsPremium(boardSkin.getIsPremium());
        existingBoardSkin.setIsActive(boardSkin.getIsActive());

        BoardSkin updatedBoardSkin = boardSkinRepository.save(existingBoardSkin);
        return ResponseEntity.ok(updatedBoardSkin);
    }

    @DeleteMapping("/board-skins/{id}")
    public ResponseEntity<Void> deleteBoardSkin(@PathVariable Long id) {
        if (!boardSkinRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        boardSkinRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================== PIECE SKIN MANAGEMENT ==================

    @GetMapping("/piece-skins")
    public ResponseEntity<List<PieceSkin>> getAllPieceSkins() {
        List<PieceSkin> pieceSkins = pieceSkinRepository.findAll();
        return ResponseEntity.ok(pieceSkins);
    }

    @PostMapping("/piece-skins")
    public ResponseEntity<PieceSkin> createPieceSkin(@RequestBody PieceSkin pieceSkin) {
        PieceSkin savedPieceSkin = pieceSkinRepository.save(pieceSkin);
        return ResponseEntity.ok(savedPieceSkin);
    }

    @PutMapping("/piece-skins/{id}")
    public ResponseEntity<PieceSkin> updatePieceSkin(@PathVariable Long id, @RequestBody PieceSkin pieceSkin) {
        Optional<PieceSkin> optionalPieceSkin = pieceSkinRepository.findById(id);
        if (!optionalPieceSkin.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        PieceSkin existingPieceSkin = optionalPieceSkin.get();
        existingPieceSkin.setName(pieceSkin.getName());
        existingPieceSkin.setDisplayName(pieceSkin.getDisplayName());
        existingPieceSkin.setDescription(pieceSkin.getDescription());
        existingPieceSkin.setPrice(pieceSkin.getPrice());
        existingPieceSkin.setXSymbol(pieceSkin.getXSymbol());
        existingPieceSkin.setOSymbol(pieceSkin.getOSymbol());
        existingPieceSkin.setXColor(pieceSkin.getXColor());
        existingPieceSkin.setOColor(pieceSkin.getOColor());
        existingPieceSkin.setXBackgroundColor(pieceSkin.getXBackgroundColor());
        existingPieceSkin.setOBackgroundColor(pieceSkin.getOBackgroundColor());
        existingPieceSkin.setCssClass(pieceSkin.getCssClass());
        existingPieceSkin.setAnimationClass(pieceSkin.getAnimationClass());
        existingPieceSkin.setIsPremium(pieceSkin.getIsPremium());
        existingPieceSkin.setIsActive(pieceSkin.getIsActive());

        PieceSkin updatedPieceSkin = pieceSkinRepository.save(existingPieceSkin);
        return ResponseEntity.ok(updatedPieceSkin);
    }

    @DeleteMapping("/piece-skins/{id}")
    public ResponseEntity<Void> deletePieceSkin(@PathVariable Long id) {
        if (!pieceSkinRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        pieceSkinRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================== STATISTICS ==================

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getAdminStats() {
        long totalUsers = userRepository.count();
        long totalActiveUsers = userRepository.countByEnabled(true);
        long totalBoardTypes = boardTypeRepository.count();
        long totalBoardSkins = boardSkinRepository.count();
        long totalPieceSkins = pieceSkinRepository.count();

        AdminStats stats = new AdminStats();
        stats.setTotalUsers(totalUsers);
        stats.setTotalActiveUsers(totalActiveUsers);
        stats.setTotalBoardTypes(totalBoardTypes);
        stats.setTotalBoardSkins(totalBoardSkins);
        stats.setTotalPieceSkins(totalPieceSkins);

        return ResponseEntity.ok(stats);
    }

    // DTO Classes
    public static class AdminStats {
        private long totalUsers;
        private long totalActiveUsers;
        private long totalBoardTypes;
        private long totalBoardSkins;
        private long totalPieceSkins;

        // Getters and setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

        public long getTotalActiveUsers() { return totalActiveUsers; }
        public void setTotalActiveUsers(long totalActiveUsers) { this.totalActiveUsers = totalActiveUsers; }

        public long getTotalBoardTypes() { return totalBoardTypes; }
        public void setTotalBoardTypes(long totalBoardTypes) { this.totalBoardTypes = totalBoardTypes; }

        public long getTotalBoardSkins() { return totalBoardSkins; }
        public void setTotalBoardSkins(long totalBoardSkins) { this.totalBoardSkins = totalBoardSkins; }

        public long getTotalPieceSkins() { return totalPieceSkins; }
        public void setTotalPieceSkins(long totalPieceSkins) { this.totalPieceSkins = totalPieceSkins; }
    }
}
