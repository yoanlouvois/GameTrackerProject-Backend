package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.GameCommentDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.*;
import com.et4.gametrackerproject.repository.GameCommentLikeRepository;
import com.et4.gametrackerproject.repository.GameCommentRepository;
import com.et4.gametrackerproject.repository.GameRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.GameCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameCommentServiceImpl implements GameCommentService {
    private static final Logger log = LoggerFactory.getLogger(GameCommentServiceImpl.class);

    private final GameCommentRepository gameCommentRepository;
    private final GameCommentLikeRepository gameCommentLikeRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameCommentServiceImpl(GameCommentRepository gameCommentRepository, GameCommentLikeRepository gameCommentLikeRepository, GameRepository gameRepository, UserRepository userRepository) {
        this.gameCommentRepository = gameCommentRepository;
        this.gameCommentLikeRepository = gameCommentLikeRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    //Crée un nouveau commentaire
    @Override
    public GameCommentDto createComment(GameCommentDto commentDto) {
        if (commentDto == null) {
            throw new IllegalArgumentException("Les données du commentaire ne peuvent être null");
        }
        // Convertir le DTO en entité
        GameComment comment = GameCommentDto.toEntity(commentDto);
        // Initialise la date de création et la date de dernière modification
        comment.setCreationDate(Instant.now());
        comment.setLastModifiedDate(Instant.now());
        // Sauvegarder le commentaire
        GameComment savedComment = gameCommentRepository.save(comment);
        log.info("Commentaire créé avec l'ID {}", savedComment.getId());
        return GameCommentDto.fromEntity(savedComment);
    }

    //Met à jour le contenu d'un commentaire.
    @Override
    public GameCommentDto updateCommentContent(Integer commentId, String newContent) {
        if (commentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut être null");
        }
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("Le nouveau contenu ne peut être null ou vide");
        }
        // Récupérer le commentaire existant
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID " + commentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));
        // Mettre à jour le contenu et la date de dernière modification
        comment.setContent(newContent);
        comment.setLastModifiedDate(Instant.now());
        // Sauvegarder les modifications
        GameComment updatedComment = gameCommentRepository.save(comment);
        log.info("Contenu du commentaire {} mis à jour", commentId);
        return GameCommentDto.fromEntity(updatedComment);
    }

    //Supprime un commentaire
    @Override
    public void deleteComment(Integer commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut être null");
        }
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID " + commentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));
        gameCommentRepository.delete(comment);
        log.info("Commentaire avec l'ID {} supprimé", commentId);

        Optional<User> users = userRepository.findByGameCommentId(commentId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer le commentaire, l'utilisateur {} a des jeux favoris associés", users.get().getUsername());
            throw new InvalidOperationException("Impossible de supprimer le commentaire, l'utilisateur a des jeux favoris associés",
                    ErrorCodes.GAME_COMMENT_ALREADY_USED);
        }

        Optional<Game> games = gameRepository.findByGameCommentId(commentId);
        if (games.isPresent()) {
            log.error("Impossible de supprimer le commentaire, le jeu {} a des jeux favoris associés", games.get().getName());
            throw new InvalidOperationException("Impossible de supprimer le commentaire, le jeu a des jeux favoris associés",
                    ErrorCodes.GAME_COMMENT_ALREADY_USED);
        }

        Optional<GameCommentLike> gameCommentLikes = gameCommentLikeRepository.findByGameCommentId(commentId);
        if (gameCommentLikes.isPresent()) {
            log.error("Impossible de supprimer le commentaire, le jeu a des jeux favoris associés");
            throw new InvalidOperationException("Impossible de supprimer le commentaire, le jeu a des jeux favoris associés",
                    ErrorCodes.GAME_COMMENT_ALREADY_USED);
        }
    }

    @Override
    public void removeAllRepliesFromComment(Integer parentCommentId) {
        if (parentCommentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire parent ne peut être null");
        }

        // Récupérer le commentaire parent
        GameComment parentComment = gameCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire parent non trouvé avec l'ID " + parentCommentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));

        // Récupérer les réponses associées au commentaire parent
        List<GameComment> replies = gameCommentRepository.findByParentComment(parentComment);

        // Supprimer toutes les réponses s'il y en a
        if (!replies.isEmpty()) {
            gameCommentRepository.deleteAll(replies);
            log.info("Suppression de {} réponses pour le commentaire parent {}", replies.size(), parentCommentId);
        } else {
            log.warn("Aucune réponse trouvée pour le commentaire parent {}", parentCommentId);
        }
    }

    @Override
    public GameCommentDto addReplyToComment(Integer parentCommentId, GameCommentDto replyDto) {
        if (parentCommentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire parent ne peut être null");
        }
        if (replyDto == null) {
            throw new IllegalArgumentException("Les données de la réponse ne peuvent être null");
        }
        // Récupérer le commentaire parent
        GameComment parentComment = gameCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire parent non trouvé avec l'ID " + parentCommentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));

        // Convertir le DTO de réponse en entité
        GameComment reply = GameCommentDto.toEntity(replyDto);
        // Définir le commentaire parent de la réponse
        reply.setParentComment(parentComment);
        // Initialiser les dates de création et de modification
        reply.setCreationDate(Instant.now());
        reply.setLastModifiedDate(Instant.now());

        // Sauvegarder la réponse
        GameComment savedReply = gameCommentRepository.save(reply);

        return GameCommentDto.fromEntity(savedReply);
    }


    //============================GETTER =======================
    @Override
    public GameCommentDto getCommentById(Integer commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut être null");
        }
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID " + commentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));
        return GameCommentDto.fromEntity(comment);
    }


    @Override
    public Page<GameCommentDto> getCommentReplies(Integer parentCommentId, Pageable pageable) {
        if (parentCommentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire parent ne peut être null");
        }
        // Récupérer le commentaire parent
        GameComment parentComment = gameCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire parent non trouvé avec l'ID " + parentCommentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));

        // On suppose que le repository dispose d'une méthode paginée pour récupérer les réponses
        Page<GameComment> replyPage = gameCommentRepository.findByParentComment(parentComment, pageable);

        return replyPage.map(GameCommentDto::fromEntity);
    }

    @Override
    public Set<UserDto> getCommentLikers(Integer commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut être null");
        }
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID " + commentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));
        List<User> likers = gameCommentLikeRepository.findUsersByComment(comment);
        return likers.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toSet());
    }

    @Override
    public Page<GameCommentDto> getCommentsForGame(Integer gameId, Pageable pageable) {
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        // Récupérer le jeu via gameRepository, et non via gameCommentLikeServiceImpl.getLikeById
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        Page<GameComment> pageComments = gameCommentRepository.findByGame(game, pageable);
        return pageComments.map(GameCommentDto::fromEntity);
    }

    // Récupère les commentaires rédigés par un utilisateur avec pagination.
    @Override
    public Page<GameCommentDto> getCommentsByUser(Integer userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        Page<GameComment> pageComments = gameCommentRepository.findByUser(user,pageable);
        return pageComments.map(GameCommentDto::fromEntity);
    }

    @Override
    public List<GameCommentDto> getRecentComments(int hours) {
        // Vérifie que le nombre d'heures est positif
        if (hours <= 0) {
            throw new IllegalArgumentException("Le nombre d'heures doit être positif");
        }
        // Calcule le seuil temporel
        Instant threshold = Instant.now().minus(hours, ChronoUnit.HOURS);
        // Récupère les commentaires créés après ce seuil
        List<GameComment> recentComments = gameCommentRepository.findByCreationDateAfter(threshold);
        // Convertit la liste d'entités en liste de DTO
        return recentComments.stream()
                .map(GameCommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GameCommentDto> searchComments(String searchTerm, Pageable pageable) {
        // Vérifie que le terme de recherche n'est pas null ou vide
        if (searchTerm == null || searchTerm.isBlank()) {
            throw new IllegalArgumentException("Le terme de recherche ne peut être null ou vide");
        }
        // Récupère les commentaires dont le contenu contient le terme recherché, en ignorant la casse, avec pagination
        Page<GameComment> pageComments = gameCommentRepository.findByContentContainingIgnoreCase(searchTerm, pageable);
        // Convertit la page d'entités en page de DTO
        return pageComments.map(GameCommentDto::fromEntity);
    }

    // Récupère les commentaires signalés avec pagination.
    @Override
    public Page<GameCommentDto> getReportedComments(Pageable pageable) {
        // Récupère la page de commentaires signalés via le repository
        Page<GameComment> reportedComments = gameCommentRepository.findReportedComments(pageable);
        // Convertit chaque entité en DTO
        return reportedComments.map(GameCommentDto::fromEntity);
    }

    // Récupère les jeux les plus commentés, limités à un certain nombre.
    @Override
    public Map<Integer, Long> getTopCommentedGames(int limit) {
        // Récupère la liste des résultats avec l'ID du jeu et le nombre de commentaires
        List<Object[]> results = gameCommentRepository.findTopCommentedGames();
        // Limite le nombre de résultats et convertit en Map
        return results.stream()
                .limit(limit)
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }


    @Override
    public List<GameCommentDto> getCommentsForGame(Integer gameId) {
        // Vérifie que l'ID du jeu n'est pas null, récupère le jeu, puis tous ses commentaires
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<GameComment> comments = gameCommentRepository.findByGame(game);
        return comments.stream()
                .map(GameCommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Long countCommentsForGame(Integer gameId) {
        // Vérifie que l'ID du jeu n'est pas null et renvoie le nombre de commentaires associés
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        return gameCommentRepository.countByGame(game);
    }

    @Override
    public Long countCommentsByUser(Integer userId) {
        // Vérifie que l'ID de l'utilisateur n'est pas null et renvoie le nombre de commentaires postés par cet utilisateur
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID " + userId, ErrorCodes.USER_NOT_FOUND));
        return gameCommentRepository.countByUser(user);
    }

    @Override
    public Long countRepliesForComment(Integer commentId) {
        // Vérifie que l'ID du commentaire parent n'est pas null et renvoie le nombre de réponses
        if (commentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire parent ne peut être null");
        }
        GameComment parentComment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire parent non trouvé avec l'ID " + commentId, ErrorCodes.GAME_COMMENT_NOT_FOUND));
        return gameCommentRepository.countReplies(parentComment);
    }

    @Override
    public List<GameCommentDto> getRecentCommentsForGame(Integer gameId) {
        // Vérifie que l'ID du jeu n'est pas null et récupère les commentaires triés par date de création décroissante
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<GameComment> comments = gameCommentRepository.findByGameOrderByCreationDateDesc(game);
        return comments.stream()
                .map(GameCommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Long> getMostLikedCommentsForGame(Integer gameId, int limit) {
        // Vérifie que l'ID du jeu n'est pas null, récupère les commentaires les plus aimés, et limite le résultat
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<Object[]> results = gameCommentRepository.findMostLikedCommentsByGame(game);
        return results.stream()
                .limit(limit)
                .collect(Collectors.toMap(
                        row -> ((GameComment) row[0]).getId(),
                        row -> ((Number) row[1]).longValue()
                ));
    }

    @Override
    public Map<Integer, Long> getMostDiscussedCommentsForGame(Integer gameId, int limit) {
        // Vérifie que l'ID du jeu n'est pas null, récupère les commentaires les plus discutés, et limite le résultat
        if (gameId == null) {
            throw new IllegalArgumentException("L'ID du jeu ne peut être null");
        }
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu non trouvé avec l'ID " + gameId, ErrorCodes.GAME_NOT_FOUND));
        List<Object[]> results = gameCommentRepository.findMostDiscussedCommentsByGame(game);
        return results.stream()
                .limit(limit)
                .collect(Collectors.toMap(
                        row -> ((GameComment) row[0]).getId(),
                        row -> ((Number) row[1]).longValue()
                ));
    }


}
