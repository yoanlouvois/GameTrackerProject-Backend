package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.GameProgressDto;
import com.et4.gametrackerproject.enums.GameStatus;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameProgress;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.GameProgressRepository;
import com.et4.gametrackerproject.repository.GameRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.GameProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameProgressServiceImpl implements GameProgressService {

    private static final Logger log = LoggerFactory.getLogger(GameProgressServiceImpl.class);


    private final GameProgressRepository gameProgressRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public GameProgressServiceImpl(GameProgressRepository gameProgressRepository, UserRepository userRepository, GameRepository gameRepository) {
        this.gameProgressRepository = gameProgressRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;

    }


    @Override
    public GameProgressDto createOrUpdateProgress(GameProgressDto progressDto) {
        // Vérifie que le DTO n'est pas null et effectue une création ou une mise à jour
        if (progressDto == null) {
            throw new IllegalArgumentException("Les données de progression ne peuvent être null");
        }
        GameProgress progress;
        if (progressDto.getId() != null) {
            // Mise à jour : récupération de l'entité existante
            progress = gameProgressRepository.findById(progressDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID "
                            + progressDto.getId(), ErrorCodes.GAME_PROGRESS_NOT_FOUND));
            // Mise à jour des champs nécessaires
            progress.setScore(progressDto.getScore());
            progress.setLastPlayed(progressDto.getLastPlayed());
            progress.setProgressData(progressDto.getProgressData());
            progress.setTimePlayed(progressDto.getTimePlayed());
            progress.setAttempts(progressDto.getAttempts());
            progress.setWins(progressDto.getWins());
            progress.setLosses(progressDto.getLosses());
            progress.setBestScore(progressDto.getBestScore());
            progress.setCurrentStreak(progressDto.getCurrentStreak());
        } else {
            // Création d'une nouvelle progression
            progress = GameProgressDto.toEntity(progressDto);
        }
        // Mise à jour de la date de dernière modification (ici, on utilise lastPlayed comme indicateur)
        progress.setLastPlayed(Instant.now());
        GameProgress saved = gameProgressRepository.save(progress);
        return GameProgressDto.fromEntity(saved);
    }



    @Override
    public void deleteGameProgressById(Integer progressId) {
        // Vérifie que l'ID n'est pas null et supprime l'entité correspondante
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID "
                        + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));

        Optional<Game> games = gameRepository.findByGameProgressId(progressId);
        if (games.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par le jeu {}", progressId, games.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par le jeu",
                    ErrorCodes.GAME_PROGRESS_ALREADY_USED);
        }

        Optional<User> users = userRepository.findByGameProgressId(progressId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par l'utilisateur {}", progressId, users.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par l'utilisateur",
                    ErrorCodes.GAME_PROGRESS_ALREADY_USED);
        }

        gameProgressRepository.delete(progress);
    }


    @Override
    public GameProgressDto startNewGameSession(Integer userId, Integer gameId) {
        // Vérifie que les IDs utilisateur et jeu ne sont pas null, récupère l'utilisateur et le jeu, puis crée une nouvelle session de jeu
        if (userId == null || gameId == null) {
            throw new IllegalArgumentException("Les IDs de l'utilisateur et du jeu ne peuvent être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));

        GameProgress newProgress = GameProgress.builder()
                .user(user)
                .game(game)
                .creationDate(Instant.now())
                .lastPlayed(Instant.now())
                .status(GameStatus.IN_PROGRESS)
                .score(0)
                .timePlayed(0)
                .attempts(0)
                .wins(0)
                .losses(0)
                .bestScore(0)
                .currentStreak(0)
                .build();

        GameProgress savedProgress = gameProgressRepository.save(newProgress);
        log.info("Nouvelle session de jeu démarrée pour l'utilisateur {} et le jeu {}", userId, gameId);
        return GameProgressDto.fromEntity(savedProgress);
    }

    @Override
    public GameProgressDto updateGameplaySession(Integer progressId, Integer scoreDelta, Integer timeDelta) {
        // Vérifie que l'ID de la progression n'est pas null, récupère la progression et met à jour le score et le temps joué
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID " + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));

        int currentScore = progress.getScore() != null ? progress.getScore() : 0;
        int deltaScore = scoreDelta != null ? scoreDelta : 0;
        int newScore = currentScore + deltaScore;
        progress.setScore(newScore);
        // Met à jour le meilleur score si nécessaire
        if (progress.getBestScore() == null || newScore > progress.getBestScore()) {
            progress.setBestScore(newScore);
        }

        int currentTime = progress.getTimePlayed() != null ? progress.getTimePlayed() : 0;
        int deltaTime = timeDelta != null ? timeDelta : 0;
        progress.setTimePlayed(currentTime + deltaTime);
        // Met à jour la date de dernière lecture
        progress.setLastPlayed(Instant.now());

        GameProgress updatedProgress = gameProgressRepository.save(progress);
        log.info("Session de jeu (ID {}) mise à jour : score +{}, temps +{}", progressId, scoreDelta, timeDelta);
        return GameProgressDto.fromEntity(updatedProgress);
    }

    @Override
    public GameProgressDto completeGame(Integer progressId) {
        // Vérifie que l'ID de la progression n'est pas null, récupère la progression et met à jour le statut en COMPLETED
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID " + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));

        progress.setStatus(GameStatus.COMPLETED);
        progress.setLastPlayed(Instant.now());

        GameProgress completedProgress = gameProgressRepository.save(progress);
        log.info("Session de jeu complétée pour l'ID {}", progressId);
        return GameProgressDto.fromEntity(completedProgress);
    }



    @Override
    public GameProgressDto getProgressById(Integer progressId) {
        // Vérifie que l'ID n'est pas null et renvoie le DTO correspondant à l'entité trouvée
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID "
                        + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));
        return GameProgressDto.fromEntity(progress);
    }

    @Override
    public GameProgressDto resetProgress(Integer progressId) {
        // Vérifie que l'ID n'est pas null et réinitialise la progression à ses valeurs par défaut
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID " + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));

        // Réinitialisation des champs
        progress.setScore(0);
        progress.setTimePlayed(0);
        progress.setAttempts(0);
        progress.setWins(0);
        progress.setLosses(0);
        progress.setBestScore(0);
        progress.setCurrentStreak(0);
        progress.setLastPlayed(Instant.now());

        GameProgress saved = gameProgressRepository.save(progress);
        log.info("Progression réinitialisée pour l'ID {}", progressId);
        return GameProgressDto.fromEntity(saved);
    }

    @Override
    public GameProgressDto recordAttempt(Integer progressId, boolean won) {
        // Vérifie que l'ID de la progression n'est pas null et enregistre une tentative, en mettant à jour les compteurs
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID " + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));

        // Incrémente le nombre d'essais
        progress.setAttempts(progress.getAttempts() != null ? progress.getAttempts() + 1 : 1);

        if (won) {
            // Si la tentative est gagnante, incrémente les victoires et la série
            progress.setWins(progress.getWins() != null ? progress.getWins() + 1 : 1);
            progress.setCurrentStreak(progress.getCurrentStreak() != null ? progress.getCurrentStreak() + 1 : 1);
        } else {
            // Sinon, incrémente les défaites et réinitialise la série
            progress.setLosses(progress.getLosses() != null ? progress.getLosses() + 1 : 1);
            progress.setCurrentStreak(0);
        }

        progress.setLastPlayed(Instant.now());
        GameProgress updated = gameProgressRepository.save(progress);
        log.info("Tentative enregistrée pour la progression {}. Gagné : {}", progressId, won);
        return GameProgressDto.fromEntity(updated);
    }

    @Override
    public GameProgressDto updateBestScore(Integer progressId, Integer newScore) {
        // Vérifie que l'ID et le nouveau score ne sont pas null et met à jour le meilleur score si nécessaire
        if (progressId == null || newScore == null) {
            throw new IllegalArgumentException("L'ID de la progression et le nouveau score ne peuvent être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID " + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));

        if (progress.getBestScore() == null || newScore > progress.getBestScore()) {
            progress.setBestScore(newScore);
            log.info("Nouveau meilleur score {} enregistré pour la progression {}", newScore, progressId);
        } else {
            log.info("Score {} inférieur au meilleur score actuel {} pour la progression {}", newScore, progress.getBestScore(), progressId);
        }

        progress.setLastPlayed(Instant.now());
        GameProgress updated = gameProgressRepository.save(progress);
        return GameProgressDto.fromEntity(updated);
    }

    @Override
    public GameProgressDto incrementStreak(Integer progressId) {
        // Vérifie que l'ID de la progression n'est pas null et incrémente la série actuelle
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID " + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));

        progress.setCurrentStreak(progress.getCurrentStreak() != null ? progress.getCurrentStreak() + 1 : 1);
        progress.setLastPlayed(Instant.now());

        GameProgress updated = gameProgressRepository.save(progress);
        log.info("Série incrémentée pour la progression {}: nouvelle série = {}", progressId, progress.getCurrentStreak());
        return GameProgressDto.fromEntity(updated);
    }

    @Override
    public GameProgressDto resetStreak(Integer progressId) {
        // Réinitialise la série (currentStreak) d'une progression donnée.
        if (progressId == null) {
            throw new IllegalArgumentException("L'ID de la progression ne peut être null");
        }
        GameProgress progress = gameProgressRepository.findById(progressId)
                .orElseThrow(() -> new EntityNotFoundException("Progression non trouvée avec l'ID " + progressId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));
        progress.setCurrentStreak(0);
        progress.setLastPlayed(Instant.now());
        GameProgress updated = gameProgressRepository.save(progress);
        log.info("Réinitialisation de la série pour la progression {}.", progressId);
        return GameProgressDto.fromEntity(updated);
    }

    @Override
    public GameProgressDto getCurrentProgress(Integer userId, Integer gameId) {
        // Récupère la progression en cours pour un utilisateur et un jeu spécifique.
        if (userId == null || gameId == null) {
            throw new IllegalArgumentException("Les IDs de l'utilisateur et du jeu ne peuvent être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        // Récupère la progression pour l'utilisateur et le jeu, et vérifie qu'elle est en cours (IN_PROGRESS)
        GameProgress progress = gameProgressRepository.findByUserAndGame(user, game)
                .filter(p -> p.getStatus() == GameStatus.IN_PROGRESS)
                .orElseThrow(() -> new EntityNotFoundException("Aucune progression en cours trouvée pour l'utilisateur "
                        + userId + " et le jeu " + gameId, ErrorCodes.GAME_PROGRESS_NOT_FOUND));
        return GameProgressDto.fromEntity(progress);
    }

    @Override
    public List<GameProgressDto> getAllUserProgress(Integer userId) {
        // Récupère toutes les progressions d'un utilisateur.
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        List<GameProgress> progresses = gameProgressRepository.findByUser(user);
        return progresses.stream()
                .map(GameProgressDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameProgressDto> getGamesByStatus(Integer userId, GameStatus status) {
        // Récupère les progressions ayant le statut COMPLETED pour un utilisateur.
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        List<GameProgress> completed = gameProgressRepository.findByUserAndStatus(user, status);
        return completed.stream()
                .map(GameProgressDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Récupère toutes les progressions pour un jeu spécifique.
    @Override
    public List<GameProgressDto> getProgressForGame(Integer gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<GameProgress> progresses = gameProgressRepository.findByGame(game);
        return progresses.stream()
                .map(GameProgressDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Récupère les progressions d'un utilisateur triées par meilleur score décroissant.
    @Override
    public List<GameProgressDto> getProgressByUserOrderByBestScoreDesc(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        List<GameProgress> progresses = gameProgressRepository.findByUserOrderByBestScoreDesc(user);
        return progresses.stream()
                .map(GameProgressDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Récupère les progressions d'un utilisateur triées par temps joué décroissant.
    @Override
    public List<GameProgressDto> getProgressByUserOrderByTimePlayedDesc(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        List<GameProgress> progresses = gameProgressRepository.findByUserOrderByTimePlayedDesc(user);
        return progresses.stream()
                .map(GameProgressDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Récupère les progressions jouées récemment par un utilisateur depuis un instant donné.
    @Override
    public List<GameProgressDto> getRecentlyPlayedGames(Integer userId, int hours) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        if (hours <= 0) {
            throw new IllegalArgumentException("Le nombre d'heures doit être positif");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Instant since = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<GameProgress> progresses = gameProgressRepository.findRecentlyPlayedGames(user, since);
        return progresses.stream()
                .map(GameProgressDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Compte le nombre de jeux par statut pour un utilisateur et retourne une Map (statut → nombre).
    @Override
    public Map<GameStatus, Long> countGamesByStatusForUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        List<Object[]> results = gameProgressRepository.countGamesByStatusForUser(user);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (GameStatus) row[0],
                        row -> ((Number) row[1]).longValue()
                ));
    }

    // Récupère le temps total de jeu d'un utilisateur.
    @Override
    public Integer getTotalPlaytimeForUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Integer totalPlaytime = gameProgressRepository.getTotalPlaytimeForUser(user);
        return totalPlaytime != null ? totalPlaytime : 0;
    }

    // Récupère les utilisateurs ayant joué à un jeu spécifique, triés par temps de jeu décroissant.
    @Override
    public Map<Integer, Integer> getUsersByGameOrderedByPlaytime(Integer gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<Object[]> results = gameProgressRepository.findUsersByGameOrderByPlaytime(game);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(), // l'ID de l'utilisateur
                        row -> ((Number) row[1]).intValue()
                ));
    }

    // Récupère les jeux les plus populaires basés sur le nombre d'utilisateurs ayant joué.
    @Override
    public Map<Integer, Long> getMostPopularGames() {
        List<Object[]> results = gameProgressRepository.findMostPopularGames();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> ((Game) row[0]).getId(),
                        row -> ((Number) row[1]).longValue()
                ));
    }

    // Récupère les utilisateurs avec le meilleur score pour un jeu, triés par score décroissant.
    @Override
    public Map<Integer, Integer> getTopScoringUsersForGame(Integer gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<Object[]> results = gameProgressRepository.findTopScoringUsersByGame(game);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> ((User) row[0]).getId(),
                        row -> ((Number) row[1]).intValue()
                ));
    }

    // Récupère les utilisateurs avec les plus longs streaks (renvoie une liste d'objets contenant l'utilisateur, le streak et le nom du jeu).
    @Override
    public List<Map<String, Object>> getUsersWithLongestStreaks() {
        List<Object[]> results = gameProgressRepository.findUsersWithLongestStreaks();
        return results.stream().map(row -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("userId", ((User) row[0]).getId());
            map.put("currentStreak", ((Number) row[1]).intValue());
            map.put("gameName", row[2]); // Supposé être le nom du jeu
            return map;
        }).collect(Collectors.toList());
    }
}
