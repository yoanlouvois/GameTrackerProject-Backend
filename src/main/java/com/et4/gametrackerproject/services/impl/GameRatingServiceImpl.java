package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.GameRatingDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameRating;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.GameRatingRepository;
import com.et4.gametrackerproject.repository.GameRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.GameRatingService;
import com.et4.gametrackerproject.services.GameService;
import com.et4.gametrackerproject.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameRatingServiceImpl implements GameRatingService {
    private static final Logger log = LoggerFactory.getLogger(GameRatingServiceImpl.class);
    private final GameRatingRepository gameRatingRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public GameRatingServiceImpl(GameRatingRepository gameRatingRepository,UserRepository userRepository, GameRepository gameRepository) {
        this.gameRatingRepository = gameRatingRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public GameRatingDto submitRating(GameRatingDto ratingDto) {
        if (ratingDto == null) {
            throw new IllegalArgumentException("Le rating ne peut être null");
        }
        // Conversion du DTO en entité et mise à jour de la date de modification
        GameRating rating = GameRatingDto.toEntity(ratingDto);
        rating.setLastModifiedDate(Instant.now());
        GameRating saved = gameRatingRepository.save(rating);
        log.info("Rating soumis avec l'ID {}", saved.getId());
        return GameRatingDto.fromEntity(saved);
    }

    @Override
    public GameRatingDto updateRating(Integer ratingId, Integer newRating) {
        if (ratingId == null || newRating == null) {
            throw new IllegalArgumentException("L'ID du rating et la nouvelle note ne peuvent être null");
        }
        GameRating existing = gameRatingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Rating non trouvé avec l'ID " + ratingId, ErrorCodes.GAME_RATING_NOT_FOUND));
        existing.setRating(newRating);
        existing.setLastModifiedDate(Instant.now());
        GameRating updated = gameRatingRepository.save(existing);
        log.info("Rating mis à jour pour l'ID {}: nouvelle note {}", ratingId, newRating);
        return GameRatingDto.fromEntity(updated);
    }

    @Override
    public void deleteRatingById(Integer ratingId) {
        if (ratingId == null) {
            throw new IllegalArgumentException("L'ID du rating ne peut être null");
        }
        GameRating rating = gameRatingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Rating non trouvé avec l'ID " + ratingId, ErrorCodes.GAME_RATING_NOT_FOUND));

        Optional<Game> games = gameRepository.findByGameRatingId(ratingId);
        if (games.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par le jeu {}", ratingId, games.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par le jeu",
                    ErrorCodes.GAME_RATING_ALREADY_USED);
        }

        Optional<User> users = userRepository.findByGameRatingId(ratingId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par l'utilisateur {}", ratingId, users.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par l'utilisateur",
                    ErrorCodes.GAME_RATING_ALREADY_USED);
        }
        gameRatingRepository.delete(rating);
    }

    //=============== GETTER ====================

    @Override
    public GameRatingDto getRatingById(Integer ratingId) {
        // Vérifie que l'ID du rating n'est pas null et récupère le rating correspondant
        if (ratingId == null) {
            throw new IllegalArgumentException("L'ID du rating ne peut être null");
        }
        GameRating rating = gameRatingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Rating non trouvé avec l'ID " + ratingId, ErrorCodes.GAME_RATING_NOT_FOUND));
        return GameRatingDto.fromEntity(rating);
    }

    @Override
    public GameRatingDto getRatingByDate(Instant date) {
        // Vérifie que la date n'est pas null et récupère le rating correspondant à cette date
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut être null");
        }
        GameRating rating = gameRatingRepository.findByLastModifiedDate(date)
                .orElseThrow(() -> new EntityNotFoundException("Rating non trouvé avec la date " + date, ErrorCodes.GAME_RATING_NOT_FOUND));
        return GameRatingDto.fromEntity(rating);
    }

    @Override
    public GameRatingDto getUserRatingForGame(Integer userId, Integer gameId) {
        // Vérifie que les IDs de l'utilisateur et du jeu ne sont pas null et récupère le rating associé
        if (userId == null || gameId == null) {
            throw new IllegalArgumentException("Les IDs de l'utilisateur et du jeu ne peuvent être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        GameRating rating = gameRatingRepository.findByUserAndGame(user, game)
                .orElseThrow(() -> new EntityNotFoundException("Rating non trouvé pour l'utilisateur " + userId + " et le jeu " + gameId, ErrorCodes.GAME_RATING_NOT_FOUND));
        return GameRatingDto.fromEntity(rating);
    }

    @Override
    public Page<GameRatingDto> getRatingsForGame(Integer gameId, Pageable pageable) {
        // Vérifie que l'ID du jeu n'est pas null et récupère les ratings pour ce jeu avec pagination
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        Page<GameRating> pageRatings = gameRatingRepository.findByGame(game, pageable);
        return pageRatings.map(GameRatingDto::fromEntity);
    }


    @Override
    public Page<GameRatingDto> getRecentRatings(Pageable pageable) {
        // Récupère les ratings récents triés par date de dernière modification décroissante.
        Page<GameRating> pageRatings = gameRatingRepository.findAllByLastModifiedDateDesc(pageable);
        return pageRatings.map(GameRatingDto::fromEntity);
    }

    @Override
    public Map<Integer, Long> getTopRatedGames(int limit) {
        // Récupère les jeux avec la meilleure note moyenne en utilisant minRating = 0.0 et limite les résultats
        List<Object[]> results = gameRatingRepository.findHighlyRatedGames(0.0);
        return results.stream()
                .limit(limit)
                .collect(Collectors.toMap(
                        row -> ((Game) row[0]).getId(),
                        row -> Math.round((Double) row[1])
                ));
    }

    //AUTRES

    @Override
    public Double calculateAverageRatingForGame(Integer gameId) {
        // Vérifie que l'ID du jeu n'est pas null, récupère le jeu et calcule la note moyenne
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        Double avgRating = gameRatingRepository.calculateAverageRatingForGame(game);
        return avgRating != null ? avgRating : 0.0;
    }

    @Override
    public Long countRatingsForGame(Integer gameId) {
        // Vérifie que l'ID du jeu n'est pas null, récupère le jeu et compte les ratings associés
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        Long count = gameRatingRepository.countByGame(game);
        return count != null ? count : 0L;
    }


    @Override
    public Page<GameRatingDto> searchRatings(String searchQuery, Pageable pageable) {
        if (searchQuery == null || searchQuery.isBlank()) {
            throw new IllegalArgumentException("La requête de recherche ne peut être null ou vide");
        }
        Page<GameRating> pageRatings = gameRatingRepository.searchRatings(searchQuery, pageable);
        return pageRatings.map(GameRatingDto::fromEntity);
    }



}
