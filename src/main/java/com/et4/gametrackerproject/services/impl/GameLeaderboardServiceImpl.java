package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.GameLeaderboardDto;
import com.et4.gametrackerproject.enums.LeaderboardPeriod;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameLeaderboard;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.GameLeaderboardRepository;
import com.et4.gametrackerproject.repository.GameRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.GameLeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameLeaderboardServiceImpl implements GameLeaderboardService {
    private static final Logger log = LoggerFactory.getLogger(GameLeaderboardServiceImpl.class);
    private final GameLeaderboardRepository gameLeaderboardRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameLeaderboardServiceImpl(GameLeaderboardRepository gameLeaderboardRepository, GameRepository gameRepository, UserRepository userRepository) {
        this.gameLeaderboardRepository = gameLeaderboardRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    // Soumet un nouveau score et retourne l'entrée sauvegardée.
    @Override
    public GameLeaderboardDto submitScore(GameLeaderboardDto scoreEntry) {
        if (scoreEntry == null) {
            throw new IllegalArgumentException("Les données du score ne peuvent être null");
        }
        GameLeaderboard entity = GameLeaderboardDto.toEntity(scoreEntry);
        entity.setLastModifiedDate(Instant.now());
        GameLeaderboard saved = gameLeaderboardRepository.save(entity);
        log.info("Score soumis avec l'ID {}", saved.getId());
        return GameLeaderboardDto.fromEntity(saved);
    }

    // Met à jour le score d'une entrée existante et retourne l'entrée mise à jour.
    @Override
    public GameLeaderboardDto updateScore(Integer entryId, Integer newScore) {
        if (entryId == null) {
            throw new IllegalArgumentException("L'ID de l'entrée ne peut être null");
        }
        if (newScore == null) {
            throw new IllegalArgumentException("Le nouveau score ne peut être null");
        }
        GameLeaderboard existing = gameLeaderboardRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("Entrée de leaderboard non trouvée avec l'ID " + entryId, ErrorCodes.GAME_LEADERBOARD_NOT_FOUND));
        existing.setScore(newScore);
        existing.setLastModifiedDate(Instant.now());
        GameLeaderboard updated = gameLeaderboardRepository.save(existing);
        log.info("Score mis à jour pour l'entrée ID {} : nouveau score {}", entryId, newScore);
        return GameLeaderboardDto.fromEntity(updated);
    }

    // Supprime une entrée de leaderboard par son ID.
    @Override
    public void deleteGameLeaderBoardById(Integer entryId) {
        if (entryId == null) {
            throw new IllegalArgumentException("L'ID de l'entrée ne peut être null");
        }
        GameLeaderboard existing = gameLeaderboardRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("Entrée de leaderboard non trouvée avec l'ID " + entryId, ErrorCodes.GAME_LEADERBOARD_NOT_FOUND));

        Optional<Game> games = gameRepository.findByGameLeaderboardId(entryId);
        if (games.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par le jeu {}", entryId, games.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par le jeu",
                    ErrorCodes.GAME_LEADERBOARD_ALREADY_USED);
        }

        Optional<User> users = userRepository.findByGameLeaderboardId(entryId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par l'utilisateur {}", entryId, users.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par l'utilisateur",
                    ErrorCodes.GAME_LEADERBOARD_ALREADY_USED);
        }

        gameLeaderboardRepository.delete(existing);
    }






    //====================GETTER ======================

    @Override
    public Page<GameLeaderboardDto> getLeaderboardForGame(Integer gameId, LeaderboardPeriod period, Pageable pageable) {
        // Vérifie que l'ID du jeu et la période ne sont pas null, récupère le jeu, et renvoie le leaderboard paginé pour ce jeu et cette période.
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        if (period == null) {
            throw new IllegalArgumentException("La période ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        Page<GameLeaderboard> pageLeaderboard = gameLeaderboardRepository.findByGameAndPeriod(game, period, pageable);
        return pageLeaderboard.map(GameLeaderboardDto::fromEntity);
    }

    @Override
    public Page<GameLeaderboardDto> getLeaderboardByPeriod(LeaderboardPeriod period, Pageable pageable) {
        // Vérifie que la période ne peut être null et renvoie le leaderboard complet paginé pour cette période.
        if (period == null) {
            throw new IllegalArgumentException("La période ne peut être null");
        }
        Page<GameLeaderboard> pageLeaderboard = gameLeaderboardRepository.findByPeriod(period, pageable);
        return pageLeaderboard.map(GameLeaderboardDto::fromEntity);
    }

    @Override
    public Optional<GameLeaderboardDto> getLeaderBoardByGameUserPeriod(Integer gameId, Integer userId, LeaderboardPeriod period) {
        // Vérifie les paramètres, récupère le jeu et l'utilisateur, puis le classement spécifique
        if (gameId == null || userId == null || period == null) {
            throw new IllegalArgumentException("L'ID du jeu, l'ID de l'utilisateur et la période ne peuvent être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Optional<GameLeaderboard> entryOpt = gameLeaderboardRepository.findByGameAndUserAndPeriod(game, user, period);
        return entryOpt.map(GameLeaderboardDto::fromEntity);
    }

    @Override
    public List<GameLeaderboardDto> getLeaderboardByGamePeriodScore(Integer gameId, LeaderboardPeriod period) {
        // Vérifie les paramètres, récupère le jeu, puis renvoie le classement complet trié par score décroissant
        if (gameId == null || period == null) {
            throw new IllegalArgumentException("L'ID du jeu et la période ne peuvent être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<GameLeaderboard> leaderboard = gameLeaderboardRepository.findByGameAndPeriodOrderByScoreDesc(game, period);
        return leaderboard.stream()
                .map(GameLeaderboardDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GameLeaderboardDto> getLeaderboardPageByRank(Integer gameId, LeaderboardPeriod period, Pageable pageable) {
        // Vérifie les paramètres, récupère le jeu, puis renvoie une page du classement trié par numéro de rang
        if (gameId == null || period == null) {
            throw new IllegalArgumentException("L'ID du jeu et la période ne peuvent être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        Page<GameLeaderboard> page = gameLeaderboardRepository.findByGameAndPeriodOrderByRankNumber(game, period, pageable);
        return page.map(GameLeaderboardDto::fromEntity);
    }

    @Override
    public List<GameLeaderboardDto> getTopRankedPlayersByGamePeriod(Integer gameId, LeaderboardPeriod period, int limit) {
        // Vérifie les paramètres, crée un Pageable pour limiter les résultats, récupère le jeu et renvoie les N meilleurs joueurs
        if (gameId == null || period == null) {
            throw new IllegalArgumentException("L'ID du jeu et la période ne peuvent être null");
        }
        Pageable pageable = PageRequest.of(0, limit);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<GameLeaderboard> topList = gameLeaderboardRepository.findTopRankedByGameAndPeriod(game, period, pageable);
        return topList.stream()
                .map(GameLeaderboardDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameLeaderboardDto> getLeaderboardEntriesForUserAndGame(Integer userId, Integer gameId) {
        // Vérifie que les IDs utilisateur et jeu ne sont pas null et récupère le classement correspondant
        if (userId == null || gameId == null) {
            throw new IllegalArgumentException("Les IDs de l'utilisateur et du jeu ne peuvent être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<GameLeaderboard> entries = gameLeaderboardRepository.findByUserAndGame(user, game);
        return entries.stream()
                .map(GameLeaderboardDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameLeaderboardDto> getLeaderboardEntriesByDate(Instant date) {
        // Vérifie que la date n'est pas null et récupère les entrées de classement correspondantes
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut être null");
        }
        List<GameLeaderboard> entries = gameLeaderboardRepository.findByDate(date);
        return entries.stream()
                .map(GameLeaderboardDto::fromEntity)
                .collect(Collectors.toList());
    }


}
