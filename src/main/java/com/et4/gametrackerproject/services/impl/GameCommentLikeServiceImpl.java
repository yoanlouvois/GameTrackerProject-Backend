package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.GameCommentLikeDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.GameComment;
import com.et4.gametrackerproject.model.GameCommentLike;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.GameCommentLikeRepository;
import com.et4.gametrackerproject.repository.GameCommentRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.GameCommentLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameCommentLikeServiceImpl implements GameCommentLikeService {
    private final GameCommentLikeRepository gameCommentLikeRepository;
    private final UserRepository userRepository;
    private final GameCommentRepository gameCommentRepository;

    private static final Logger log = LoggerFactory.getLogger(GameCommentLikeServiceImpl.class);


    public GameCommentLikeServiceImpl(GameCommentLikeRepository gameCommentLikeRepository,
                                      UserRepository userRepository,
                                      GameCommentRepository gameCommentRepository) {
        this.gameCommentLikeRepository = gameCommentLikeRepository;
        this.userRepository = userRepository;
        this.gameCommentRepository = gameCommentRepository;
    }

    @Override
    public GameCommentLikeDto addCommentLike(Integer userId, Integer commentId) {
        if (userId == null || commentId == null) {
            throw new IllegalArgumentException("User ID and Comment ID must not be null");
        }

        // Retrieve the user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with ID: " + userId,
                        ErrorCodes.USER_NOT_FOUND));

        // Retrieve the comment entity
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Game comment not found with ID: " + commentId,
                        ErrorCodes.GAME_COMMENT_NOT_FOUND));

        // Check if the like already exists
        Optional<GameCommentLike> existingLike = gameCommentLikeRepository.findByUserAndComment(user, comment);
        if (existingLike.isPresent()) {
            return GameCommentLikeDto.fromEntity(existingLike.get());
        }

        // Create a new like
        GameCommentLike newLike = GameCommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
        GameCommentLike savedLike = gameCommentLikeRepository.save(newLike);
        log.info("User {} liked comment {}", userId, commentId);
        return GameCommentLikeDto.fromEntity(savedLike);
    }

    @Override
    public void deleteGameCommentLikeById(Integer likeId) {
        if (likeId == null) {
            throw new IllegalArgumentException("Like ID must not be null");
        }
        GameCommentLike like = gameCommentLikeRepository.findById(likeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Like not found with ID: " + likeId,
                        ErrorCodes.GAME_COMMENT_LIKE_NOT_FOUND));

        Optional<GameComment> gameComments = gameCommentRepository.findByGameCommentLikeId(likeId);
        if (gameComments.isPresent()) {
            log.error("Impossible de supprimer le like, car il est associé à un commentaire");
            throw new InvalidOperationException("Impossible de supprimer le like, car il est associé à un commentaire",
                    ErrorCodes.GAME_COMMENT_LIKE_ALREADY_USED);
        }

        Optional<User> users = userRepository.findByGameCommentLikeId(likeId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer le like, car il est associé à un utilisateur");
            throw new InvalidOperationException("Impossible de supprimer le like, car il est associé à un utilisateur",
                    ErrorCodes.GAME_COMMENT_LIKE_ALREADY_USED);
        }

        gameCommentLikeRepository.delete(like);

    }


    //==================== GETTER =======================

    @Override
    public GameCommentLikeDto getLikeById(Integer likeId) {
        if (likeId == null) {
            throw new IllegalArgumentException("L'ID du like ne peut être null");
        }
        GameCommentLike like = gameCommentLikeRepository.findById(likeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Like introuvable avec l'ID " + likeId,
                        ErrorCodes.GAME_COMMENT_LIKE_NOT_FOUND));
        return GameCommentLikeDto.fromEntity(like);
    }

    @Override
    public Page<GameCommentLikeDto> getLikesForComment(Integer commentId, Pageable pageable) {
        if (commentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut être null");
        }
        // Retrieve the comment first
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Commentaire introuvable avec l'ID " + commentId,
                        ErrorCodes.GAME_COMMENT_NOT_FOUND));
        // Retrieve the paged likes for the comment
        Page<GameCommentLike> likePage = gameCommentLikeRepository.findByComment(comment, pageable);
        // Map each entity to its DTO
        return likePage.map(GameCommentLikeDto::fromEntity);
    }

    @Override
    public Page<GameCommentLikeDto> getLikesByUser(Integer userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut être null");
        }
        // Retrieve the user first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Utilisateur introuvable avec l'ID " + userId,
                        ErrorCodes.USER_NOT_FOUND));
        // Retrieve the paged likes for the user
        Page<GameCommentLike> likePage = gameCommentLikeRepository.findByUser(user,pageable);
        // Map to DTO
        return likePage.map(GameCommentLikeDto::fromEntity);
    }

    //Récupère le nombre de likes pour un commentaire.
    @Override
    public Long getLikeCountForComment(Integer commentId) {
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé", ErrorCodes.GAME_COMMENT_NOT_FOUND));
        return gameCommentLikeRepository.countByComment(comment);
    }

    //Récupère les commentaires les plus aimés, limités à un certain nombre.
    @Override
    public Map<Integer, Long> getMostLikedComments(int limit) {
        List<Object[]> results = gameCommentLikeRepository.findMostLikedComments();
        return results.stream()
                .limit(limit)
                .collect(Collectors.toMap(
                        row -> ((GameComment) row[0]).getId(),
                        row -> (Long) row[1]
                ));
    }

    //Récupère les likes récents avec pagination
    @Override
    public Page<GameCommentLikeDto> getRecentLikes(Pageable pageable) {
        return gameCommentLikeRepository.findRecentLikes(pageable)
                .map(GameCommentLikeDto::fromEntity);
    }

    //Récupère tous les likes avec pagination
    @Override
    public Page<GameCommentLikeDto> getAllLikes(Pageable pageable) {
        return gameCommentLikeRepository.findAll(pageable)
                .map(GameCommentLikeDto::fromEntity);
    }

    @Override
    public Long countLikesByUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }
        // Récupération de l'utilisateur via le repository (à adapter si vous disposez d'un UserRepository)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé", ErrorCodes.USER_NOT_FOUND));
        return gameCommentLikeRepository.countByUser(user);
    }

    @Override
    public List<UserDto> getUsersWhoLikedComment(Integer commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut être null");
        }
        // Récupération du commentaire via le repository (à adapter si vous disposez d'un GameCommentRepository)
        GameComment comment = gameCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé", ErrorCodes.GAME_COMMENT_NOT_FOUND));
        List<User> users = gameCommentLikeRepository.findUsersByComment(comment);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }


}
