package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.GameProgressDto;
import com.et4.gametrackerproject.enums.GameStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface GameProgressApi {

    // Opérations de base
    @PostMapping(value = APP_ROOT + "/progress/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une progression de jeu", description = "Créer une progression de jeu")
    @ApiResponse(responseCode = "200", description = "Progression de jeu créée ")
    GameProgressDto createProgress(@RequestBody GameProgressDto progressDto);

    @DeleteMapping(value = APP_ROOT + "/progress/{progressId}")
    @Operation(summary = "Supprimer une progression de jeu", description = "Supprimer une progression de jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progression de jeu supprimée"),
            @ApiResponse(responseCode = "404", description = "Progression de jeu non trouvée")
    })
    void deleteProgress(@PathVariable("progressId") Integer progressId);

    //Gestion de la progression
    @PostMapping(value = APP_ROOT + "/progress/start", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Démarrer une nouvelle session de jeu", description = "Démarrer une nouvelle session de jeu")
    @ApiResponse(responseCode = "200", description = "Session de jeu démarrée")
    GameProgressDto startNewGameSession(@RequestParam("userId") Integer userId, @RequestParam("gameId") Integer gameId);

    @PutMapping(value = APP_ROOT + "/progress/{progressId}/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour la session de jeu", description = "Mettre à jour la session de jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session de jeu mise à jour"),
            @ApiResponse(responseCode = "404", description = "Session de jeu non trouvée")
    })
    GameProgressDto updateGameplaySession(@PathVariable("progressId") Integer progressId, @RequestParam("scoreDelta") Integer scoreDelta, @RequestParam("timeDelta") Integer timeDelta);

    @PutMapping(value = APP_ROOT + "/progress/{progressId}/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Marquer la session de jeu comme terminée", description = "Marquer la session de jeu comme terminée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session de jeu marquée comme terminée"),
            @ApiResponse(responseCode = "404", description = "Session de jeu non trouvée")
    })
    GameProgressDto completeGame(@PathVariable("progressId") Integer progressId);

    @GetMapping(value = APP_ROOT + "/progress/{progressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer une progression de jeu par son ID", description = "Récupérer une progression de jeu par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progression de jeu trouvée"),
            @ApiResponse(responseCode = "404", description = "Progression de jeu non trouvée")
    })
    GameProgressDto getProgressById(@PathVariable("progressId") Integer progressId);

    @PutMapping(value = APP_ROOT + "/progress/{progressId}/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Réinitialiser la progression de jeu", description = "Réinitialiser la progression de jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progression de jeu réinitialisée"),
            @ApiResponse(responseCode = "404", description = "Progression de jeu non trouvée")
    })
    GameProgressDto resetProgress(@PathVariable("progressId") Integer progressId);

    //Suivi des performances
    @PutMapping(value = APP_ROOT + "/progress/{progressId}/attempt", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une tentative de jeu", description = "Enregistrer une tentative de jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tentative de jeu enregistrée"),
            @ApiResponse(responseCode = "404", description = "Progression de jeu non trouvée")
    })
    GameProgressDto recordAttempt(@PathVariable("progressId") Integer progressId, @RequestParam("won") boolean won);

    @PutMapping(value = APP_ROOT + "/progress/{progressId}/score", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour le meilleur score", description = "Mettre à jour le meilleur score")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meilleur score mis à jour"),
            @ApiResponse(responseCode = "404", description = "Progression de jeu non trouvée")
    })
    GameProgressDto updateBestScore(@PathVariable("progressId") Integer progressId, @RequestParam("newScore") Integer newScore);

    @PutMapping(value = APP_ROOT + "/progress/{progressId}/streak/increment", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Incrémenter le streak", description = "Incrémenter le streak")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Streak incrémenté"),
            @ApiResponse(responseCode = "404", description = "Progression de jeu non trouvée")
    })
    GameProgressDto incrementStreak(@PathVariable("progressId") Integer progressId);

    @PutMapping(value = APP_ROOT + "/progress/{progressId}/streak/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Réinitialiser le streak", description = "Réinitialiser le streak")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Streak réinitialisé"),
            @ApiResponse(responseCode = "404", description = "Progression de jeu non trouvée")
    })
    GameProgressDto resetStreak(@PathVariable("progressId") Integer progressId);

    // Récupération des données
    @GetMapping(value = APP_ROOT + "/progress/current", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer la progression actuelle d'un utilisateur pour un jeu", description = "Récupérer la progression actuelle d'un utilisateur pour un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progression actuelle trouvée"),
            @ApiResponse(responseCode = "404", description = "Progression actuelle non trouvée")
    })
    GameProgressDto getCurrentProgress(@RequestParam("userId") Integer userId, @RequestParam("gameId") Integer gameId);

    @GetMapping(value = APP_ROOT + "/progress/user/{userId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer toutes les progressions d'un utilisateur", description = "Récupérer toutes les progressions d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progressions d'utilisateur trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune progression trouvée pour cet utilisateur")
    })
    List<GameProgressDto> getAllUserProgress(@PathVariable("userId") Integer userId);

    @GetMapping(value = APP_ROOT + "/progress/user/{userId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les progressions d'un utilisateur par statut", description = "Récupérer les progressions d'un utilisateur par statut")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progressions d'utilisateur trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune progression trouvée pour cet utilisateur")
    })
    List<GameProgressDto> getGamesByStatus(@PathVariable Integer userId, GameStatus status);

    // Récupère toutes les progressions pour un jeu spécifique.
    @GetMapping(value = APP_ROOT + "/progress/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer toutes les progressions pour un jeu", description = "Récupérer toutes les progressions pour un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progressions de jeu trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune progression trouvée pour ce jeu")
    })
    List<GameProgressDto> getProgressForGame(@PathVariable Integer gameId);

    // Récupère les progressions d'un utilisateur triées par meilleur score décroissant.
    @GetMapping(value = APP_ROOT + "/progress/user/{userId}/best-score", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les progressions d'un utilisateur triées par meilleur score", description = "Récupérer les progressions d'un utilisateur triées par meilleur score")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progressions d'utilisateur triées par meilleur score trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune progression trouvée pour cet utilisateur")
    })
    List<GameProgressDto> getProgressByUserOrderByBestScoreDesc(@PathVariable Integer userId);

    // Récupère les progressions d'un utilisateur triées par temps joué décroissant.
    @GetMapping(value = APP_ROOT + "/progress/user/{userId}/time-played", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les progressions d'un utilisateur triées par temps joué", description = "Récupérer les progressions d'un utilisateur triées par temps joué")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progressions d'utilisateur triées par temps joué trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune progression trouvée pour cet utilisateur")
    })
    List<GameProgressDto> getProgressByUserOrderByTimePlayedDesc(@PathVariable Integer userId);

    // Récupère les progressions jouées récemment par un utilisateur depuis un instant donné.
    @GetMapping(value = APP_ROOT + "/progress/user/{userId}/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux récemment joués par un utilisateur", description = "Récupérer les jeux récemment joués par un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jeux récemment joués trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé pour cet utilisateur")
    })
    List<GameProgressDto> getRecentlyPlayedGames(@PathVariable Integer userId, int hours);

    // Compte le nombre de jeux par statut pour un utilisateur et retourne une Map (statut -> nombre).
    @GetMapping(value = APP_ROOT + "/progress/user/{userId}/count-by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre de jeux par statut pour un utilisateur", description = "Compter le nombre de jeux par statut pour un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nombre de jeux par statut trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé pour cet utilisateur")
    })
    Map<GameStatus, Long> countGamesByStatusForUser(@PathVariable Integer userId);

    // Récupère le temps total de jeu d'un utilisateur.
    @GetMapping(value = APP_ROOT + "/progress/user/{userId}/total-playtime", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le temps total de jeu d'un utilisateur", description = "Récupérer le temps total de jeu d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Temps total de jeu trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé pour cet utilisateur")
    })
    Integer getTotalPlaytimeForUser(@PathVariable Integer userId);

    // Récupère les utilisateurs ayant joué à un jeu spécifique, triés par temps de jeu décroissant.
    @GetMapping(value = APP_ROOT + "/progress/game/{gameId}/users-by-playtime", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les utilisateurs ayant joué à un jeu spécifique", description = "Récupérer les utilisateurs ayant joué à un jeu spécifique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateurs trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé pour ce jeu")
    })
    Map<Integer, Integer> getUsersByGameOrderedByPlaytime(@PathVariable Integer gameId);

    // Récupère les jeux les plus populaires basés sur le nombre d'utilisateurs ayant joué.
    @GetMapping(value = APP_ROOT + "/progress/most-popular-games", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux les plus populaires", description = "Récupérer les jeux les plus populaires")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jeux populaires trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Map<Integer, Long> getMostPopularGames();

    // Récupère les utilisateurs avec le meilleur score pour un jeu, triés par score décroissant.
    @GetMapping(value = APP_ROOT + "/progress/game/{gameId}/top-scoring-users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les utilisateurs avec le meilleur score pour un jeu", description = "Récupérer les utilisateurs avec le meilleur score pour un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateurs avec le meilleur score trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé pour ce jeu")
    })
    Map<Integer, Integer> getTopScoringUsersForGame(@PathVariable Integer gameId);

    // Récupère les utilisateurs avec les plus longs streaks (renvoie une liste d'objets contenant l'utilisateur, le streak et le nom du jeu).
    @GetMapping(value = APP_ROOT + "/progress/longest-streaks", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les utilisateurs avec les plus longs streaks", description = "Récupérer les utilisateurs avec les plus longs streaks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateurs avec les plus longs streaks trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé")
    })
    List<Map<String, Object>> getUsersWithLongestStreaks();




}