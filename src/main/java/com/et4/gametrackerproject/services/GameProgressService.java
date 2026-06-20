package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.GameProgressDto;
import com.et4.gametrackerproject.enums.GameStatus;

import java.util.List;
import java.util.Map;

public interface GameProgressService {

    // Opérations de base
    GameProgressDto createOrUpdateProgress(GameProgressDto progressDto);
    GameProgressDto getProgressById(Integer progressId);
    void deleteGameProgressById(Integer progressId);

    //Gestion de la progression
    GameProgressDto startNewGameSession(Integer userId, Integer gameId);
    GameProgressDto updateGameplaySession(Integer progressId, Integer scoreDelta, Integer timeDelta);
    GameProgressDto completeGame(Integer progressId);
    GameProgressDto resetProgress(Integer progressId);

    //Suivi des performances
    GameProgressDto recordAttempt(Integer progressId, boolean won);
    GameProgressDto updateBestScore(Integer progressId, Integer newScore);
    GameProgressDto incrementStreak(Integer progressId);
    GameProgressDto resetStreak(Integer progressId);

    // Récupération des données
    GameProgressDto getCurrentProgress(Integer userId, Integer gameId);
    List<GameProgressDto> getAllUserProgress(Integer userId);

    List<GameProgressDto> getGamesByStatus(Integer userId, GameStatus status);

    // Récupère toutes les progressions pour un jeu spécifique.
    List<GameProgressDto> getProgressForGame(Integer gameId);

    // Récupère les progressions d'un utilisateur triées par meilleur score décroissant.
    List<GameProgressDto> getProgressByUserOrderByBestScoreDesc(Integer userId);

    // Récupère les progressions d'un utilisateur triées par temps joué décroissant.
    List<GameProgressDto> getProgressByUserOrderByTimePlayedDesc(Integer userId);

    // Récupère les progressions jouées récemment par un utilisateur depuis un instant donné.
    List<GameProgressDto> getRecentlyPlayedGames(Integer userId, int hours);

    // Compte le nombre de jeux par statut pour un utilisateur et retourne une Map (statut -> nombre).
    Map<GameStatus, Long> countGamesByStatusForUser(Integer userId);

    // Récupère le temps total de jeu d'un utilisateur.
    Integer getTotalPlaytimeForUser(Integer userId);

    // Récupère les utilisateurs ayant joué à un jeu spécifique, triés par temps de jeu décroissant.
// Retourne une Map associant l'ID de l'utilisateur au temps joué.
    Map<Integer, Integer> getUsersByGameOrderedByPlaytime(Integer gameId);

    // Récupère les jeux les plus populaires basés sur le nombre d'utilisateurs ayant joué.
    Map<Integer, Long> getMostPopularGames();

    // Récupère les utilisateurs avec le meilleur score pour un jeu, triés par score décroissant.
    Map<Integer, Integer> getTopScoringUsersForGame(Integer gameId);

    // Récupère les utilisateurs avec les plus longs streaks (renvoie une liste d'objets contenant l'utilisateur, le streak et le nom du jeu).
    List<Map<String, Object>> getUsersWithLongestStreaks();
}