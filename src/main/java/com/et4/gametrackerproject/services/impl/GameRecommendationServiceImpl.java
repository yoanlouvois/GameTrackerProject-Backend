package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.GameRecommendationDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameRecommendation;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.GameRecommendationRepository;
import com.et4.gametrackerproject.repository.GameRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.GameRecommendationService;
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
public class GameRecommendationServiceImpl implements GameRecommendationService {
    private static final Logger log = LoggerFactory.getLogger(GameRecommendationServiceImpl.class);

    private final GameRecommendationRepository gameRecommendationRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public GameRecommendationServiceImpl(GameRecommendationRepository gameRecommendationRepository,
                                         UserRepository userRepository,
                                         GameRepository gameRepository) {
        this.gameRecommendationRepository = gameRecommendationRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    // Crée une recommandation de jeu
    @Override
    public GameRecommendationDto createRecommendation(Integer senderId, Integer receiverId, Integer gameId, String message) {
        if (senderId == null || receiverId == null || gameId == null) {
            throw new IllegalArgumentException("Les IDs de l'expéditeur, du destinataire et du jeu doivent être fournis");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Le message ne peut être null ou vide");
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Expéditeur non trouvé avec l'ID " + senderId, ErrorCodes.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID " + receiverId, ErrorCodes.USER_NOT_FOUND));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));

        GameRecommendation recommendation = GameRecommendation.builder()
                .sender(sender)
                .receiver(receiver)
                .game(game)
                .message(message)
                .creationDate(Instant.now())
                .build();

        GameRecommendation saved = gameRecommendationRepository.save(recommendation);
        log.info("Recommandation créée avec l'ID {}", saved.getId());
        return GameRecommendationDto.fromEntity(saved);
    }

    // Met à jour le message d'une recommandation existante
    @Override
    public GameRecommendationDto updateRecommendationMessage(Integer recommendationId, String newMessage) {
        if (recommendationId == null) {
            throw new IllegalArgumentException("L'ID de la recommandation ne peut être null");
        }
        if (newMessage == null || newMessage.isBlank()) {
            throw new IllegalArgumentException("Le nouveau message ne peut être null ou vide");
        }
        GameRecommendation recommendation = gameRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommandation non trouvée avec l'ID " + recommendationId, ErrorCodes.GAME_RECOMMENDATION_NOT_FOUND));

        recommendation.setMessage(newMessage);
        // Vous pouvez éventuellement mettre à jour la date de modification ici, si nécessaire
        GameRecommendation updated = gameRecommendationRepository.save(recommendation);
        log.info("Message de la recommandation {} mis à jour", recommendationId);
        return GameRecommendationDto.fromEntity(updated);
    }

    // Supprime une recommandation
    @Override
    public void deleteRecommendationById(Integer recommendationId) {
        if (recommendationId == null) {
            throw new IllegalArgumentException("L'ID de la recommandation ne peut être null");
        }
        GameRecommendation recommendation = gameRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommandation non trouvée avec l'ID " + recommendationId, ErrorCodes.GAME_RECOMMENDATION_NOT_FOUND));

        Optional<Game> games = gameRepository.findByGameRecommendationId(recommendationId);
        if (games.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par le jeu {}", recommendationId, games.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par le jeu",
                    ErrorCodes.GAME_RECOMMENDATION_ALREADY_USED);
        }

        Optional<User> users = userRepository.findByGameRecommendationId(recommendationId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer l'entrée de leaderboard avec l'ID {} car elle est utilisée par l'utilisateur {}", recommendationId, users.get().getId());
            throw new InvalidOperationException("Impossible de supprimer l'entrée de leaderboard car elle est utilisée par l'utilisateur",
                    ErrorCodes.GAME_RECOMMENDATION_ALREADY_USED);
        }


        gameRecommendationRepository.delete(recommendation);
    }


    //============================= GETTER ==============================

    @Override
    public GameRecommendationDto getRecommendationById(Integer recommendationId) {
        // Vérifie que l'ID n'est pas null et récupère la recommandation correspondante
        if (recommendationId == null) {
            throw new IllegalArgumentException("L'ID de la recommandation ne peut être null");
        }
        GameRecommendation recommendation = gameRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommandation non trouvée avec l'ID " + recommendationId, ErrorCodes.GAME_RECOMMENDATION_NOT_FOUND));
        return GameRecommendationDto.fromEntity(recommendation);
    }

    @Override
    public Page<GameRecommendationDto> getRecommendationsBySender(Integer senderId, Pageable pageable) {
        // Vérifie que l'ID de l'expéditeur n'est pas null et récupère l'expéditeur
        if (senderId == null) {
            throw new IllegalArgumentException("L'ID de l'expéditeur ne peut être null");
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Expéditeur non trouvé avec l'ID " + senderId, ErrorCodes.USER_NOT_FOUND));
        // Récupère les recommandations envoyées par cet utilisateur avec pagination
        Page<GameRecommendation> page = gameRecommendationRepository.findBySender(sender, pageable);
        return page.map(GameRecommendationDto::fromEntity);
    }

    @Override
    public Page<GameRecommendationDto> getRecommendationsByReceiver(Integer receiverId, Pageable pageable) {
        // Vérifie que l'ID du destinataire n'est pas null et récupère le destinataire
        if (receiverId == null) {
            throw new IllegalArgumentException("L'ID du destinataire ne peut être null");
        }
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID " + receiverId, ErrorCodes.USER_NOT_FOUND));
        // Récupère les recommandations reçues par cet utilisateur avec pagination
        Page<GameRecommendation> page = gameRecommendationRepository.findByReceiver(receiver, pageable);
        return page.map(GameRecommendationDto::fromEntity);
    }

    @Override
    public Page<GameRecommendationDto> getRecommendationsBetweenUsers(Integer user1Id, Integer user2Id, Pageable pageable) {
        // Vérification des paramètres
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent être null");
        }

        // Récupération des utilisateurs
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + user1Id, ErrorCodes.USER_NOT_FOUND));

        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + user2Id, ErrorCodes.USER_NOT_FOUND));

        // Récupérer les recommandations entre ces deux utilisateurs
        Page<GameRecommendation> recommendations = gameRecommendationRepository.findBySenderAndReceiver(user1, user2, pageable);

        return recommendations.map(GameRecommendationDto::fromEntity);
    }

    @Override
    public Page<GameRecommendationDto> getAllRecommendations(Pageable pageable) {
        // Récupère toutes les recommandations avec pagination
        Page<GameRecommendation> recommendations = gameRecommendationRepository.findAll(pageable);
        return recommendations.map(GameRecommendationDto::fromEntity);
    }

    @Override
    public Map<Integer, Long> getMostRecommendedGames(Pageable pageable) {
        // Exécute une requête pour obtenir les jeux les plus recommandés, limités au nombre donné
        List<Object[]> results = gameRecommendationRepository.findMostRecommendedGames(pageable);

        // Convertit les résultats en un map {gameId : nombre de recommandations}
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],  // gameId
                        result -> (Long) result[1]       // count
                ));
    }


    //COUNT

    @Override
    public Long countRecommendationsForGame(Integer gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        return gameRecommendationRepository.countByGame(game);
    }

    @Override
    public Long countRecommendationsReceivedByUser(Integer receiverId) {
        if (receiverId == null) {
            throw new IllegalArgumentException("L'ID du destinataire ne peut être null");
        }
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + receiverId, ErrorCodes.USER_NOT_FOUND));

        return gameRecommendationRepository.countByReceiver(receiver);
    }

    @Override
    public Long countRecommendationsSentByUser(Integer senderId) {
        if (senderId == null) {
            throw new IllegalArgumentException("L'ID de l'expéditeur ne peut être null");
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + senderId, ErrorCodes.USER_NOT_FOUND));

        return gameRecommendationRepository.countBySender(sender);
    }

    //SEARCH

    @Override
    public Page<GameRecommendationDto> searchRecommendations(String searchQuery, Pageable pageable) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("La requête de recherche ne peut être vide");
        }
        Page<GameRecommendation> recommendations = gameRecommendationRepository
                .findByMessageContainingIgnoreCase(searchQuery, pageable);
        return recommendations.map(GameRecommendationDto::fromEntity);
    }






}
