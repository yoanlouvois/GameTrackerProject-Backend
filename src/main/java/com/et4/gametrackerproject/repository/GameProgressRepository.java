package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.enums.GameStatus;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameProgress;
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

public interface GameProgressRepository extends JpaRepository<GameProgress,Integer> {

    // Trouver la progression d'un utilisateur pour un jeu spécifique
    Optional<GameProgress> findByUserAndGame(User user, Game game);

    // Récupérer la liste des progressions d'un utilisateur
    List<GameProgress> findByUser(User user);

    // Récupérer la liste des progressions d'un utilisateur avec pagination
    Page<GameProgress> findByUser(User user, Pageable pageable);

    // Récupérer les progressions d'un utilisateur filtrées par statut
    List<GameProgress> findByUserAndStatus(User user, GameStatus status);

    // Récupérer la liste des progressions pour un jeu spécifique
    List<GameProgress> findByGame(Game game);

    // Rechercher les jeux avec les meilleurs scores d'un utilisateur
    List<GameProgress> findByUserOrderByBestScoreDesc(User user);

    // Rechercher les jeux avec le plus de temps joué par un utilisateur
    List<GameProgress> findByUserOrderByTimePlayedDesc(User user);

    // Rechercher les progressions actualisées récemment
    @Query("SELECT gp FROM GameProgress gp WHERE gp.user = :user AND gp.lastPlayed >= :since ORDER BY gp.lastPlayed DESC")
    List<GameProgress> findRecentlyPlayedGames(@Param("user") User user, @Param("since") Instant since);

    // Compter le nombre de jeux par statut pour un utilisateur
    @Query("SELECT gp.status, COUNT(gp) FROM GameProgress gp WHERE gp.user = :user GROUP BY gp.status")
    List<Object[]> countGamesByStatusForUser(@Param("user") User user);

    // Récupérer le temps total de jeu d'un utilisateur
    @Query("SELECT SUM(gp.timePlayed) FROM GameProgress gp WHERE gp.user = :user")
    Integer getTotalPlaytimeForUser(@Param("user") User user);

    // Récupérer les utilisateurs qui ont joué à un jeu spécifique, triés par temps de jeu
    @Query("SELECT gp.user, gp.timePlayed FROM GameProgress gp WHERE gp.game = :game ORDER BY gp.timePlayed DESC")
    List<Object[]> findUsersByGameOrderByPlaytime(@Param("game") Game game);

    // Récupérer les jeux les plus populaires basés sur le nombre d'utilisateurs
    @Query("SELECT gp.game, COUNT(gp.user) as userCount FROM GameProgress gp GROUP BY gp.game ORDER BY userCount DESC")
    List<Object[]> findMostPopularGames();

    // Récupérer les utilisateurs avec le meilleur score pour un jeu
    @Query("SELECT gp.user, gp.bestScore FROM GameProgress gp WHERE gp.game = :game ORDER BY gp.bestScore DESC")
    List<Object[]> findTopScoringUsersByGame(@Param("game") Game game);

    // Récupérer les utilisateurs avec les plus longs streaks
    @Query("SELECT gp.user, gp.currentStreak, gp.game.name FROM GameProgress gp ORDER BY gp.currentStreak DESC")
    List<Object[]> findUsersWithLongestStreaks();


    // Récupérer les progress par gameId
    @Query("SELECT gp FROM GameProgress gp WHERE gp.game.id = :gameId")
    Optional<GameProgress> findByGameId(Integer id);
}