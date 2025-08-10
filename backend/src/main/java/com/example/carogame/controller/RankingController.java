package com.example.carogame.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carogame.entity.UserStats;
import com.example.carogame.repository.UserStatsRepository;
import com.example.carogame.service.RankingService;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private RankingService rankingService;

    // Get global leaderboard by ranking points
    @GetMapping("/leaderboard")
    public ResponseEntity<Page<UserStats>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserStats> leaderboard = userStatsRepository.findLeaderboardByRankingPoints(pageable);
        return ResponseEntity.ok(leaderboard);
    }

    // Get leaderboard by win rate (minimum 10 games)
    @GetMapping("/leaderboard/winrate")
    public ResponseEntity<Page<UserStats>> getLeaderboardByWinRate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "10") int minGames) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserStats> leaderboard = userStatsRepository.findLeaderboardByWinRate(minGames, pageable);
        return ResponseEntity.ok(leaderboard);
    }

    // Get leaderboard by total wins
    @GetMapping("/leaderboard/wins")
    public ResponseEntity<Page<UserStats>> getLeaderboardByWins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserStats> leaderboard = userStatsRepository.findLeaderboardByTotalWins(pageable);
        return ResponseEntity.ok(leaderboard);
    }

    // Get leaderboard by best streak
    @GetMapping("/leaderboard/streak")
    public ResponseEntity<Page<UserStats>> getLeaderboardByStreak(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserStats> leaderboard = userStatsRepository.findLeaderboardByBestStreak(pageable);
        return ResponseEntity.ok(leaderboard);
    }

    // Get top 10 players
    @GetMapping("/top10")
    public ResponseEntity<List<UserStats>> getTop10() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserStats> top10 = userStatsRepository.findTop10ByRankingPoints(pageable);
        return ResponseEntity.ok(top10);
    }

    // Get users with current winning streak
    @GetMapping("/streaks")
    public ResponseEntity<List<UserStats>> getCurrentStreaks(
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<UserStats> streaks = userStatsRepository.findUsersWithCurrentStreak(pageable);
        return ResponseEntity.ok(streaks);
    }

    // Get most active players today
    @GetMapping("/active-today")
    public ResponseEntity<List<UserStats>> getMostActiveToday(
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<UserStats> activeToday = userStatsRepository.findMostActiveToday(pageable);
        return ResponseEntity.ok(activeToday);
    }

    // Get user's rank position
    @GetMapping("/position/{userId}")
    public ResponseEntity<Map<String, Object>> getUserRankPosition(@PathVariable Long userId) {
        Integer position = userStatsRepository.getUserRankPosition(userId);
        UserStats userStats = userStatsRepository.findByUserId(userId).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("position", position);
        response.put("stats", userStats);

        return ResponseEntity.ok(response);
    }

    // Get ranking statistics summary
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getRankingSummary() {
        Object[] summary = userStatsRepository.getStatsSummary();

        Map<String, Object> response = new HashMap<>();
        if (summary != null && summary.length >= 4) {
            response.put("totalPlayers", summary[0]);
            response.put("averageRating", summary[1]);
            response.put("highestRating", summary[2]);
            response.put("lowestRating", summary[3]);
        }

        return ResponseEntity.ok(response);
    }

    // Get players by rating range
    @GetMapping("/range")
    public ResponseEntity<List<UserStats>> getPlayersByRatingRange(
            @RequestParam Integer minPoints,
            @RequestParam Integer maxPoints) {

        List<UserStats> players = userStatsRepository.findByRankingPointsRange(minPoints, maxPoints);
        return ResponseEntity.ok(players);
    }

    // Get recently active players
    @GetMapping("/recent-active")
    public ResponseEntity<List<UserStats>> getRecentlyActive(
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(defaultValue = "20") int limit) {

        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        Pageable pageable = PageRequest.of(0, limit);
        List<UserStats> recentlyActive = userStatsRepository.findRecentlyActive(since, pageable);
        return ResponseEntity.ok(recentlyActive);
    }

    // Update rankings (admin only)
    @GetMapping("/update")
    public ResponseEntity<Map<String, String>> updateRankings() {
        try {
            rankingService.updateAllRankings();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Rankings updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update rankings: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get user stats by username
    @GetMapping("/user/{username}")
    public ResponseEntity<UserStats> getUserStats(@PathVariable String username) {
        UserStats stats = userStatsRepository.findByUsername(username).orElse(null);
        if (stats != null) {
            return ResponseEntity.ok(stats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
