package com.example.carogame.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.carogame.entity.User;
import com.example.carogame.entity.UserStats;
import com.example.carogame.repository.UserRepository;
import com.example.carogame.repository.UserStatsRepository;

@Service
public class RankingService {

    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private UserRepository userRepository;

    // ELO rating calculation constants
    private static final int K_FACTOR = 32; // K-factor for ELO calculation
    private static final double EXPECTED_SCORE_DIVISOR = 400.0;

    /**
     * Calculate ELO rating change based on game result
     */
    public int calculateEloChange(int playerRating, int opponentRating, double actualScore) {
        double expectedScore = 1.0 / (1.0 + Math.pow(10, (opponentRating - playerRating) / EXPECTED_SCORE_DIVISOR));
        return (int) Math.round(K_FACTOR * (actualScore - expectedScore));
    }

    /**
     * Update user stats after a game
     */
    @Transactional
    public void updateUserStatsAfterGame(Long winnerId, Long loserId, boolean isDraw) {
        UserStats winnerStats = getOrCreateUserStats(winnerId);
        UserStats loserStats = getOrCreateUserStats(loserId);

        if (isDraw) {
            // Handle draw - small rating adjustment
            int winnerChange = calculateEloChange(winnerStats.getRankingPoints(),
                                                loserStats.getRankingPoints(), 0.5);
            int loserChange = calculateEloChange(loserStats.getRankingPoints(),
                                               winnerStats.getRankingPoints(), 0.5);

            winnerStats.addDraw();
            loserStats.addDraw();

            winnerStats.setRankingPoints(winnerStats.getRankingPoints() + winnerChange);
            loserStats.setRankingPoints(loserStats.getRankingPoints() + loserChange);
        } else {
            // Handle win/loss
            int winnerChange = calculateEloChange(winnerStats.getRankingPoints(),
                                                loserStats.getRankingPoints(), 1.0);
            int loserChange = calculateEloChange(loserStats.getRankingPoints(),
                                               winnerStats.getRankingPoints(), 0.0);

            winnerStats.addWin(Math.max(1, winnerChange));
            loserStats.addLoss(Math.max(1, Math.abs(loserChange)));
        }

        userStatsRepository.save(winnerStats);
        userStatsRepository.save(loserStats);
    }

    /**
     * Get or create user stats for a user
     */
    private UserStats getOrCreateUserStats(Long userId) {
        return userStatsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    UserStats newStats = new UserStats(user);
                    return userStatsRepository.save(newStats);
                });
    }

    /**
     * Update all user rank positions based on current ratings
     */
    @Transactional
    public void updateAllRankings() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<UserStats> allStats = userStatsRepository.findTop10ByRankingPoints(pageable);

        for (int i = 0; i < allStats.size(); i++) {
            UserStats stats = allStats.get(i);
            stats.setRankPosition(i + 1);
            userStatsRepository.save(stats);
        }
    }

    /**
     * Reset daily statistics for all users
     */
    @Transactional
    public void resetDailyStats() {
        userStatsRepository.resetAllDailyStats();
    }

    /**
     * Get user's current rank tier based on rating
     */
    public String getUserRankTier(int rating) {
        if (rating >= 2000) return "Grandmaster";
        else if (rating >= 1800) return "Master";
        else if (rating >= 1600) return "Diamond";
        else if (rating >= 1400) return "Platinum";
        else if (rating >= 1200) return "Gold";
        else if (rating >= 1000) return "Silver";
        else if (rating >= 800) return "Bronze";
        else return "Unranked";
    }

    /**
     * Get rank tier color for UI
     */
    public String getRankTierColor(String tier) {
        switch (tier) {
            case "Grandmaster": return "#ff6b35";
            case "Master": return "#e74c3c";
            case "Diamond": return "#9b59b6";
            case "Platinum": return "#3498db";
            case "Gold": return "#f1c40f";
            case "Silver": return "#95a5a6";
            case "Bronze": return "#d35400";
            default: return "#7f8c8d";
        }
    }

    /**
     * Calculate win rate percentage
     */
    public double calculateWinRate(int wins, int totalGames) {
        if (totalGames == 0) return 0.0;
        return (double) wins / totalGames * 100.0;
    }

    /**
     * Get performance rating based on recent games
     */
    public String getPerformanceRating(UserStats stats) {
        double winRate = stats.getWinRate();
        int currentStreak = stats.getCurrentStreak();

        if (winRate >= 80 && currentStreak >= 5) return "Exceptional";
        else if (winRate >= 70 && currentStreak >= 3) return "Excellent";
        else if (winRate >= 60) return "Good";
        else if (winRate >= 50) return "Average";
        else if (winRate >= 40) return "Below Average";
        else return "Needs Improvement";
    }

    /**
     * Check if user is eligible for rank promotion
     */
    public boolean isEligibleForPromotion(UserStats stats) {
        // Criteria: Win rate > 60%, at least 10 games, current streak >= 3
        return stats.getWinRate() > 60.0 &&
               stats.getTotalGames() >= 10 &&
               stats.getCurrentStreak() >= 3;
    }

    /**
     * Get recommended opponents based on similar rating
     */
    public List<UserStats> getRecommendedOpponents(Long userId, int limit) {
        UserStats userStats = userStatsRepository.findByUserId(userId).orElse(null);
        if (userStats == null) return List.of();

        int userRating = userStats.getRankingPoints();
        int minRating = Math.max(0, userRating - 200);
        int maxRating = userRating + 200;

        List<UserStats> candidates = userStatsRepository.findByRankingPointsRange(minRating, maxRating);
        return candidates.stream()
                .filter(stats -> !stats.getUser().getId().equals(userId))
                .limit(limit)
                .toList();
    }
}
