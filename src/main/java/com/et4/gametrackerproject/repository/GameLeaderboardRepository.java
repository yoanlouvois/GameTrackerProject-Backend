package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.enums.LeaderboardPeriod;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameLeaderboard;
import com.et4.gametrackerproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GameLeaderboardRepository extends JpaRepository<GameLeaderboard,Integer> {

    // Récupérer tous les classements d'un utilisateur
    List<GameLeaderboard> findByUser(User user);

    @Query("SELECT gl FROM GameLeaderboard gl WHERE gl.game = :game AND gl.period = :period")
    Page<GameLeaderboard> findByGameAndPeriod(@Param("game") Game game, @Param("period") LeaderboardPeriod period, Pageable pageable);

    @Query("SELECT gl FROM GameLeaderboard gl WHERE gl.period = :period")
    Page<GameLeaderboard> findByPeriod(@Param("period") LeaderboardPeriod period, Pageable pageable);

    // Trouver le classement d'un utilisateur pour un jeu et une période
    Optional<GameLeaderboard> findByGameAndUserAndPeriod(Game game, User user, LeaderboardPeriod period);

    // Récupérer le tableau de classement complet pour un jeu et une période, trié par score
    List<GameLeaderboard> findByGameAndPeriodOrderByScoreDesc(Game game, LeaderboardPeriod period);

    // Pagination du tableau de classement
    Page<GameLeaderboard> findByGameAndPeriodOrderByRankNumber(Game game, LeaderboardPeriod period, Pageable pageable);

    // Récupérer les N meilleurs joueurs pour un jeu et une période
    @Query("SELECT gl FROM GameLeaderboard gl WHERE gl.game = :game AND gl.period = :period ORDER BY gl.rankNumber ASC")
    List<GameLeaderboard> findTopRankedByGameAndPeriod(@Param("game") Game game,
                                                       @Param("period") LeaderboardPeriod period,
                                                       Pageable pageable);

    // Récupérer tous les classements d'un utilisateur pour un jeu spécifique
    List<GameLeaderboard> findByUserAndGame(User user, Game game);

    // Récupérer les entrées de classement par date spécifique
    List<GameLeaderboard> findByDate(Instant date);




    void deleteGameLeaderboardByGame(Game game);

    // Supprimer le classement d'un utilisateur pour un jeu id
    @Query("SELECT gl FROM GameLeaderboard gl WHERE gl.user = :user AND gl.game.id = :gameId")
    Optional<GameLeaderboard> findByGameId(Integer id);
}
