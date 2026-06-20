package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.DailyGameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DailyGameSessionRepository extends JpaRepository<DailyGameSession,Integer> {

    //=============================================Recherche==========================================
    List<DailyGameSession> findByDate(Instant date);

    @Query("SELECT d FROM DailyGameSession d WHERE d.user.id = :userId")
    List<DailyGameSession> findByUser(Integer userId);

    @Query("SELECT d FROM DailyGameSession d WHERE d.user.id = :userId AND d.date = :date")
    Optional<DailyGameSession> findByUserAndDate(@Param("userId") Integer userId, @Param("date") Instant date);

    @Query("SELECT d FROM DailyGameSession d WHERE d.user.id = :userId AND d.date BETWEEN :start AND :end")
    List<DailyGameSession> findByUserBetweenDates(@Param("userId") Integer userId,
                                                  @Param("start") Instant start,
                                                  @Param("end") Instant end);

    // Récupérer la session avec le plus de temps de jeu pour un utilisateur
    @Query("SELECT d FROM DailyGameSession d WHERE d.user.id = :userId ORDER BY d.totalTimePlayed DESC")
    List<DailyGameSession> findLongestSessionsByUser(@Param("userId") Integer userId);

    // Récupérer les N dernières sessions d'un utilisateur
    @Query("SELECT d FROM DailyGameSession d WHERE d.user = :user ORDER BY d.date DESC")
    List<DailyGameSession> findRecentSessionsByUser(@Param("userId") Integer userId);

    // Récupérer les utilisateurs les plus actifs (en termes de temps de jeu total)
    @Query("SELECT d.user, SUM(d.totalTimePlayed) as totalTime FROM DailyGameSession d GROUP BY d.user ORDER BY totalTime DESC")
    List<Object[]> findMostActiveUsers();

    @Query("SELECT MAX(d.date) FROM DailyGameSession d WHERE d.user.id = :userId")
    Instant findLastPlayedDateByUserId(@Param("userId") Integer userId);


    //=============================================CALCULS==========================================

    // Calcul du temps de jeu total pour un utilisateur
    @Query("SELECT SUM(d.totalTimePlayed) FROM DailyGameSession d WHERE d.user.id = :userId")
    Integer calculateTotalPlaytimeByUser(@Param("userId") Integer userId);

    // Calcul du temps de jeu total pour un utilisateur sur une période donnée
    @Query("SELECT SUM(d.totalTimePlayed) FROM DailyGameSession d WHERE d.user.id = :userId AND d.date BETWEEN :startDate AND :endDate")
    Integer calculatePlaytimeByUserInPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    // Obtenir le nombre total de sessions de jeu pour un utilisateur
    @Query("SELECT COUNT(d) FROM DailyGameSession d WHERE d.user.id = :userId")
    Long countSessionsByUser(@Param("userId") Integer userId);

    // Obtenir le nombre total de jeux joués par un utilisateur
    @Query("SELECT COUNT(d.gamesPlayed) FROM DailyGameSession d WHERE d.user.id = :userId")
    Integer countGamesPlayedByUser(@Param("userId") Integer userId);

    // Calculer la moyenne quotidienne de jeu pour un utilisateur
    @Query("SELECT AVG(d.gamesPlayed) FROM DailyGameSession d WHERE d.user.id = :userId")
    Double calculateAveragePlaytimeByUser(@Param("userId") Integer userId);

}
