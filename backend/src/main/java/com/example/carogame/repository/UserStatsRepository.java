package com.example.carogame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.carogame.entity.UserStats;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {

    // Find stats by user ID
    Optional<UserStats> findByUserId(Long userId);

    // Find stats by username
    @Query("SELECT us FROM UserStats us JOIN us.user u WHERE u.username = :username")
    Optional<UserStats> findByUsername(@Param("username") String username);

    // Get leaderboard ordered by ranking points
    @Query("SELECT us FROM UserStats us ORDER BY us.rankingPoints DESC")
    Page<UserStats> findLeaderboardByRankingPoints(Pageable pageable);

    // Get leaderboard ordered by win rate (minimum games required)
    @Query("SELECT us FROM UserStats us WHERE us.totalGames >= :minGames ORDER BY us.winRate DESC")
    Page<UserStats> findLeaderboardByWinRate(@Param("minGames") int minGames, Pageable pageable);

    // Get leaderboard ordered by total wins
    @Query("SELECT us FROM UserStats us ORDER BY us.totalWins DESC")
    Page<UserStats> findLeaderboardByTotalWins(Pageable pageable);

    // Get leaderboard ordered by best streak
    @Query("SELECT us FROM UserStats us ORDER BY us.bestStreak DESC")
    Page<UserStats> findLeaderboardByBestStreak(Pageable pageable);

    // Get top players by ranking points
    @Query("SELECT us FROM UserStats us ORDER BY us.rankingPoints DESC")
    List<UserStats> findTop10ByRankingPoints(Pageable pageable);

    // Get users with current streak
    @Query("SELECT us FROM UserStats us WHERE us.currentStreak > 0 ORDER BY us.currentStreak DESC")
    List<UserStats> findUsersWithCurrentStreak(Pageable pageable);

    // Get most active players today
    @Query("SELECT us FROM UserStats us WHERE us.gamesToday > 0 ORDER BY us.gamesToday DESC")
    List<UserStats> findMostActiveToday(Pageable pageable);

    // Update rank positions
    @Modifying
    @Query("UPDATE UserStats us SET us.rankPosition = :position WHERE us.id = :id")
    void updateRankPosition(@Param("id") Long id, @Param("position") Integer position);

    // Reset daily stats for all users
    @Modifying
    @Query("UPDATE UserStats us SET us.gamesToday = 0, us.winsToday = 0")
    void resetAllDailyStats();

    // Get user rank position
    @Query("SELECT COUNT(us) + 1 FROM UserStats us WHERE us.rankingPoints > " +
           "(SELECT us2.rankingPoints FROM UserStats us2 WHERE us2.user.id = :userId)")
    Integer getUserRankPosition(@Param("userId") Long userId);

    // Get stats summary
    @Query("SELECT COUNT(us), AVG(us.rankingPoints), MAX(us.rankingPoints), MIN(us.rankingPoints) " +
           "FROM UserStats us")
    Object[] getStatsSummary();

    // Find users by ranking points range
    @Query("SELECT us FROM UserStats us WHERE us.rankingPoints BETWEEN :minPoints AND :maxPoints " +
           "ORDER BY us.rankingPoints DESC")
    List<UserStats> findByRankingPointsRange(@Param("minPoints") Integer minPoints,
                                           @Param("maxPoints") Integer maxPoints);

    // Get recent active players
    @Query("SELECT us FROM UserStats us WHERE us.lastGameDate >= :since ORDER BY us.lastGameDate DESC")
    List<UserStats> findRecentlyActive(@Param("since") java.time.LocalDateTime since, Pageable pageable);
}
