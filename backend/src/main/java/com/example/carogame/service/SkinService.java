package com.example.carogame.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carogame.entity.BoardSkin;
import com.example.carogame.entity.PieceSkin;
import com.example.carogame.entity.User;
import com.example.carogame.entity.UserSkin;
import com.example.carogame.repository.BoardSkinRepository;
import com.example.carogame.repository.PieceSkinRepository;
import com.example.carogame.repository.UserRepository;
import com.example.carogame.repository.UserSkinRepository;

@Service
@Transactional
public class SkinService {

    @Autowired
    private BoardSkinRepository boardSkinRepository;

    @Autowired
    private PieceSkinRepository pieceSkinRepository;

    @Autowired
    private UserSkinRepository userSkinRepository;

    @Autowired
    private UserRepository userRepository;

    // Board Skin Methods
    public List<BoardSkin> getAllActiveBoardSkins() {
        return boardSkinRepository.findByIsActiveTrue();
    }

    public List<BoardSkin> getAffordableBoardSkins(Long userBalance) {
        return boardSkinRepository.findByPriceLessThanEqualAndIsActiveTrue(userBalance);
    }

    public Optional<BoardSkin> getBoardSkinByName(String name) {
        return boardSkinRepository.findByName(name);
    }

    // Piece Skin Methods
    public List<PieceSkin> getAllActivePieceSkins() {
        return pieceSkinRepository.findByIsActiveTrue();
    }

    public List<PieceSkin> getAffordablePieceSkins(Long userBalance) {
        return pieceSkinRepository.findByPriceLessThanEqualAndIsActiveTrue(userBalance);
    }

    public Optional<PieceSkin> getPieceSkinByName(String name) {
        return pieceSkinRepository.findByName(name);
    }

    // User Skin Methods
    public List<UserSkin> getUserSkins(User user) {
        return userSkinRepository.findByUser(user);
    }

    public List<UserSkin> getUserBoardSkins(User user) {
        return userSkinRepository.findByUserAndSkinType(user, "BOARD");
    }

    public List<UserSkin> getUserPieceSkins(User user) {
        return userSkinRepository.findByUserAndSkinType(user, "PIECE");
    }

    public boolean userOwnsSkin(User user, String skinType, String skinName) {
        return userSkinRepository.existsByUserAndSkinTypeAndSkinName(user, skinType, skinName);
    }

    // Purchase Methods
    @Transactional
    public boolean purchaseBoardSkin(Long userId, String skinName) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<BoardSkin> skinOpt = boardSkinRepository.findByName(skinName);

        if (userOpt.isEmpty() || skinOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        BoardSkin skin = skinOpt.get();

        // Check if user already owns this skin
        if (userOwnsSkin(user, "BOARD", skinName)) {
            return false;
        }

        // Check if user has enough balance
        if (user.getBalance() < skin.getPrice()) {
            return false;
        }

        // Deduct balance and save purchase
        user.spendCoins(skin.getPrice());
        userRepository.save(user);

        UserSkin userSkin = new UserSkin(user, "BOARD", skinName, skin.getPrice());
        userSkinRepository.save(userSkin);

        return true;
    }

    @Transactional
    public boolean purchasePieceSkin(Long userId, String skinName) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<PieceSkin> skinOpt = pieceSkinRepository.findByName(skinName);

        if (userOpt.isEmpty() || skinOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        PieceSkin skin = skinOpt.get();

        // Check if user already owns this skin
        if (userOwnsSkin(user, "PIECE", skinName)) {
            return false;
        }

        // Check if user has enough balance
        if (user.getBalance() < skin.getPrice()) {
            return false;
        }

        // Deduct balance and save purchase
        user.spendCoins(skin.getPrice());
        userRepository.save(user);

        UserSkin userSkin = new UserSkin(user, "PIECE", skinName, skin.getPrice());
        userSkinRepository.save(userSkin);

        return true;
    }

    // Skin Selection Methods
    @Transactional
    public boolean selectBoardSkin(Long userId, String skinName) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Check if user owns this skin or if it's the default skin
        if (!skinName.equals("classic") && !userOwnsSkin(user, "BOARD", skinName)) {
            return false;
        }

        user.setSelectedBoardSkin(skinName);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean selectPieceSkin(Long userId, String skinName) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Check if user owns this skin or if it's the default skin
        if (!skinName.equals("classic") && !userOwnsSkin(user, "PIECE", skinName)) {
            return false;
        }

        user.setSelectedPieceSkin(skinName);
        userRepository.save(user);
        return true;
    }

    // Initialize default skins
    @Transactional
    public void initializeDefaultSkins() {
        // Initialize default board skins if they don't exist
        if (boardSkinRepository.findByName("classic").isEmpty()) {
            BoardSkin classicBoard = new BoardSkin("classic", "Classic", "The traditional board style", 0L, "classic-board");
            classicBoard.setBackgroundColor("#f9f9f9");
            classicBoard.setBorderColor("#333");
            classicBoard.setCellColor("#ffffff");
            classicBoard.setHoverColor("#e8f4fd");
            boardSkinRepository.save(classicBoard);
        }

        // Initialize default piece skins if they don't exist
        if (pieceSkinRepository.findByName("classic").isEmpty()) {
            PieceSkin classicPiece = new PieceSkin("classic", "Classic", "Traditional X and O pieces", 0L, "X", "O");
            classicPiece.setXColor("#e74c3c");
            classicPiece.setOColor("#3498db");
            classicPiece.setXBackgroundColor("#ffeaea");
            classicPiece.setOBackgroundColor("#eaf4fd");
            classicPiece.setCssClass("classic-piece");
            pieceSkinRepository.save(classicPiece);
        }
    }
}
