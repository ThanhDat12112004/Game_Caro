package com.example.carogame.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_wins", nullable = false)
    private Integer totalWins = 0;

    @Column(name = "total_losses", nullable = false)
    private Integer totalLosses = 0;

    @Column(name = "total_draws", nullable = false)
    private Integer totalDraws = 0;

    @Column(name = "total_games", nullable = false)
    private Integer totalGames = 0;

    @Column(name = "win_rate", nullable = false)
    private Double winRate = 0.0;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Column(name = "best_streak", nullable = false)
    private Integer bestStreak = 0;

    @Column(name = "ranking_points", nullable = false)
    private Integer rankingPoints = 1000; // ELO-style rating system

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(name = "games_today", nullable = false)
    private Integer gamesToday = 0;

    @Column(name = "wins_today", nullable = false)
    private Integer winsToday = 0;

    @Column(name = "last_game_date")
    private LocalDateTime lastGameDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public UserStats() {}

    public UserStats(User user) {
        this.user = user;
        this.totalWins = 0;
        this.totalLosses = 0;
        this.totalDraws = 0;
        this.totalGames = 0;
        this.winRate = 0.0;
        this.currentStreak = 0;
        this.bestStreak = 0;
        this.rankingPoints = 1000;
        this.gamesToday = 0;
        this.winsToday = 0;
        this.updatedAt = LocalDateTime.now();
    }

    // Methods to update stats
    public void addWin(int pointsGained) {
        this.totalWins++;
        this.totalGames++;
        this.currentStreak++;
        this.winsToday++;
        this.gamesToday++;
        this.rankingPoints += pointsGained;

        if (this.currentStreak > this.bestStreak) {
            this.bestStreak = this.currentStreak;
        }

        updateWinRate();
        this.lastGameDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addLoss(int pointsLost) {
        this.totalLosses++;
        this.totalGames++;
        this.currentStreak = 0;
        this.gamesToday++;
        this.rankingPoints = Math.max(0, this.rankingPoints - pointsLost);

        updateWinRate();
        this.lastGameDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addDraw() {
        this.totalDraws++;
        this.totalGames++;
        this.gamesToday++;
        // No streak change for draws

        updateWinRate();
        this.lastGameDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void updateWinRate() {
        if (this.totalGames > 0) {
            this.winRate = (double) this.totalWins / this.totalGames * 100;
        } else {
            this.winRate = 0.0;
        }
    }

    public void resetDailyStats() {
        this.gamesToday = 0;
        this.winsToday = 0;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(Integer totalWins) {
        this.totalWins = totalWins;
    }

    public Integer getTotalLosses() {
        return totalLosses;
    }

    public void setTotalLosses(Integer totalLosses) {
        this.totalLosses = totalLosses;
    }

    public Integer getTotalDraws() {
        return totalDraws;
    }

    public void setTotalDraws(Integer totalDraws) {
        this.totalDraws = totalDraws;
    }

    public Integer getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Integer totalGames) {
        this.totalGames = totalGames;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(Integer bestStreak) {
        this.bestStreak = bestStreak;
    }

    public Integer getRankingPoints() {
        return rankingPoints;
    }

    public void setRankingPoints(Integer rankingPoints) {
        this.rankingPoints = rankingPoints;
    }

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }

    public Integer getGamesToday() {
        return gamesToday;
    }

    public void setGamesToday(Integer gamesToday) {
        this.gamesToday = gamesToday;
    }

    public Integer getWinsToday() {
        return winsToday;
    }

    public void setWinsToday(Integer winsToday) {
        this.winsToday = winsToday;
    }

    public LocalDateTime getLastGameDate() {
        return lastGameDate;
    }

    public void setLastGameDate(LocalDateTime lastGameDate) {
        this.lastGameDate = lastGameDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
