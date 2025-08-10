package com.example.carogame.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.carogame.entity.User;
import com.example.carogame.model.BoardType;
import com.example.carogame.model.GameState;
import com.example.carogame.model.Move;
import com.example.carogame.model.Player;
import com.example.carogame.repository.UserRepository;

@Service
public class GameService {

    @Autowired
    private UserRepository userRepository;

    // Reward amounts
    private static final Long WIN_REWARD = 50L;
    private static final Long PARTICIPATION_REWARD = 10L;
    private Map<String, GameState> games = new HashMap<>();

    public GameState createGame(String gameId) {
        return createGame(gameId, BoardType.STANDARD);
    }

    public GameState createGame(String gameId, BoardType boardType) {
        GameState gameState = new GameState(gameId, boardType);
        gameState.setPlayers(new ArrayList<>());
        games.put(gameId, gameState);
        return gameState;
    }

    public GameState getGame(String gameId) {
        return games.get(gameId);
    }

    public GameState joinGame(String gameId, Player player) {
        GameState game = games.get(gameId);
        if (game == null) {
            game = createGame(gameId);
        }

        if (game.getPlayers().size() < 2) {
            if (game.getPlayers().isEmpty()) {
                player.setSymbol("X");
                game.setCurrentPlayer(player.getId());
            } else {
                player.setSymbol("O");
            }
            game.getPlayers().add(player);
        }

        return game;
    }

    public GameState makeMove(String gameId, Move move) {
        GameState game = games.get(gameId);
        if (game == null || game.isGameOver()) {
            return game;
        }

        // Validate move
        if (game.getBoard()[move.getRow()][move.getCol()] != null) {
            return game; // Invalid move
        }

        // Make move
        game.getBoard()[move.getRow()][move.getCol()] = move.getSymbol();

        // Check for winner
        if (checkWinner(game, move.getRow(), move.getCol(), move.getSymbol())) {
            game.setGameOver(true);
            game.setWinner(move.getPlayerId());

            // Award coins to players
            awardCoinsForGameEnd(game, move.getPlayerId());
        } else {
            // Switch turns
            String nextPlayer = game.getPlayers().stream()
                    .filter(p -> !p.getId().equals(move.getPlayerId()))
                    .findFirst()
                    .map(Player::getId)
                    .orElse(null);
            game.setCurrentPlayer(nextPlayer);
        }

        return game;
    }

    private boolean checkWinner(GameState game, int row, int col, String symbol) {
        String[][] board = game.getBoard();
        int boardSize = game.getBoardSize();

        // Check horizontal
        int count = 1;
        for (int i = col - 1; i >= 0 && symbol.equals(board[row][i]); i--) count++;
        for (int i = col + 1; i < boardSize && symbol.equals(board[row][i]); i++) count++;
        if (count >= 5) return true;

        // Check vertical
        count = 1;
        for (int i = row - 1; i >= 0 && symbol.equals(board[i][col]); i--) count++;
        for (int i = row + 1; i < boardSize && symbol.equals(board[i][col]); i++) count++;
        if (count >= 5) return true;

        // Check diagonal (top-left to bottom-right)
        count = 1;
        for (int i = 1; row - i >= 0 && col - i >= 0 && symbol.equals(board[row - i][col - i]); i++) count++;
        for (int i = 1; row + i < boardSize && col + i < boardSize && symbol.equals(board[row + i][col + i]); i++) count++;
        if (count >= 5) return true;

        // Check diagonal (top-right to bottom-left)
        count = 1;
        for (int i = 1; row - i >= 0 && col + i < boardSize && symbol.equals(board[row - i][col + i]); i++) count++;
        for (int i = 1; row + i < boardSize && col - i >= 0 && symbol.equals(board[row + i][col - i]); i++) count++;
        if (count >= 5) return true;

        return false;
    }

    public void resetGame(String gameId) {
        GameState game = games.get(gameId);
        if (game != null) {
            int boardSize = game.getBoardSize();
            game.setBoard(new String[boardSize][boardSize]);
            game.setGameOver(false);
            game.setWinner(null);
            if (!game.getPlayers().isEmpty()) {
                game.setCurrentPlayer(game.getPlayers().get(0).getId());
            }
        }
    }

    public Map<String, Object> getAllGamesInfo() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> gamesList = new ArrayList<>();

        for (Map.Entry<String, GameState> entry : games.entrySet()) {
            GameState game = entry.getValue();
            Map<String, Object> gameInfo = new HashMap<>();
            gameInfo.put("gameId", entry.getKey());
            gameInfo.put("boardType", game.getBoardType().name());
            gameInfo.put("boardSize", game.getBoardSize());
            gameInfo.put("boardDescription", game.getBoardType().getDescription());
            gameInfo.put("playerCount", game.getPlayers().size());
            gameInfo.put("maxPlayers", 2);
            gameInfo.put("gameStatus", game.isGameOver() ? "FINISHED" : "PLAYING");
            gameInfo.put("canJoin", game.getPlayers().size() < 2);

            gamesList.add(gameInfo);
        }

        result.put("games", gamesList);
        result.put("totalGames", gamesList.size());
        return result;
    }

    /**
     * Xóa người chơi khỏi phòng và tự động xóa phòng nếu không còn ai
     */
    public GameState removePlayerFromGame(String gameId, String playerId) {
        GameState game = games.get(gameId);
        if (game == null) {
            return null;
        }

        // Tìm và xóa người chơi
        game.getPlayers().removeIf(player -> player.getId().equals(playerId));

        // Nếu không còn người chơi nào, xóa phòng hoàn toàn
        if (game.getPlayers().isEmpty()) {
            games.remove(gameId);
            return null;
        }

        // Nếu vẫn còn 1 người chơi, reset game và đặt người đó làm current player
        if (game.getPlayers().size() == 1) {
            resetGame(gameId);
            game.setCurrentPlayer(game.getPlayers().get(0).getId());
            game.setGameOver(false);
            game.setWinner(null);
        }

        return game;
    }

    /**
     * Xử lý khi người chơi thoát khỏi phòng
     */
    public GameState leaveGame(String gameId, String playerId) {
        GameState game = games.get(gameId);
        if (game == null) {
            return null;
        }

        // Xóa người chơi khỏi danh sách
        game.getPlayers().removeIf(player -> player.getId().equals(playerId));

        // Nếu không còn người chơi nào, xóa phòng
        if (game.getPlayers().isEmpty()) {
            games.remove(gameId);
            return null;
        }

        // Nếu chỉ còn 1 người chơi và game đang diễn ra, kết thúc game
        if (game.getPlayers().size() == 1 && !game.isGameOver()) {
            game.setGameOver(true);
            game.setWinner(game.getPlayers().get(0).getId());
        }

        return game;
    }

    /**
     * Xóa phòng game
     */
    public boolean removeGame(String gameId) {
        return games.remove(gameId) != null;
    }

    /**
     * Kiểm tra xem người chơi có trong phòng không
     */
    public boolean isPlayerInGame(String gameId, String playerId) {
        GameState game = games.get(gameId);
        if (game == null) {
            return false;
        }

        return game.getPlayers().stream()
                .anyMatch(player -> player.getId().equals(playerId));
    }

    /**
     * Lấy danh sách tất cả người chơi trong phòng
     */
    public List<Player> getPlayersInGame(String gameId) {
        GameState game = games.get(gameId);
        if (game == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(game.getPlayers());
    }

    /**
     * Xóa các phòng không có người chơi (cleanup định kỳ)
     */
    public int cleanupEmptyGames() {
        int removedCount = 0;
        List<String> gameIdsToRemove = new ArrayList<>();

        for (Map.Entry<String, GameState> entry : games.entrySet()) {
            if (entry.getValue().getPlayers().isEmpty()) {
                gameIdsToRemove.add(entry.getKey());
            }
        }

        for (String gameId : gameIdsToRemove) {
            games.remove(gameId);
            removedCount++;
        }

        return removedCount;
    }

    /**
     * Lấy tổng số phòng hiện tại
     */
    public int getTotalGamesCount() {
        return games.size();
    }

    /**
     * Award coins to players when game ends
     */
    private void awardCoinsForGameEnd(GameState game, String winnerId) {
        try {
            for (Player player : game.getPlayers()) {
                if (player.getUserId() != null) {
                    User user = userRepository.findByUsername(player.getUserId()).orElse(null);
                    if (user != null) {
                        if (player.getId().equals(winnerId)) {
                            // Winner gets win reward
                            user.addCoins(WIN_REWARD);
                            user.addWin();
                        } else {
                            // Loser gets participation reward
                            user.addCoins(PARTICIPATION_REWARD);
                            user.addLoss();
                        }
                        userRepository.save(user);
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't break game flow
            System.err.println("Error awarding coins: " + e.getMessage());
        }
    }
}
