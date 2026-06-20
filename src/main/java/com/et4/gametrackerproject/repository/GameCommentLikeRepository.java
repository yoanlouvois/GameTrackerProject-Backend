package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.GameComment;
import com.et4.gametrackerproject.model.GameCommentLike;
import com.et4.gametrackerproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GameCommentLikeRepository extends JpaRepository<GameCommentLike,Integer> {

    // Vérifier si un utilisateur a aimé un commentaire spécifique
    Optional<GameCommentLike> findByUserAndComment(User user, GameComment comment);

    Page<GameCommentLike> findByComment(GameComment comment, Pageable pageable);

    // Récupérer tous les likes d'un utilisateur
    Page<GameCommentLike> findByUser(User user, Pageable pageable);

    // Compter le nombre de likes pour un commentaire
    @Query("SELECT COUNT(gcl) FROM GameCommentLike gcl WHERE gcl.comment = :comment")
    Long countByComment(@Param("comment") GameComment comment);

    @Query("SELECT gcl FROM GameCommentLike gcl ORDER BY gcl.creationDate DESC")
    Page<GameCommentLike> findRecentLikes(Pageable pageable);

    // Compter le nombre total de likes donnés par un utilisateur
    @Query("SELECT COUNT(gcl) FROM GameCommentLike gcl WHERE gcl.user = :user")
    Long countByUser(@Param("user") User user);

    // Récupérer les utilisateurs qui ont aimé un commentaire spécifique
    @Query("SELECT gcl.user FROM GameCommentLike gcl WHERE gcl.comment = :comment")
    List<User> findUsersByComment(@Param("comment") GameComment comment);

    // Récupérer les commentaires les plus aimés
    @Query("SELECT gcl.comment, COUNT(gcl) as likeCount FROM GameCommentLike gcl GROUP BY gcl.comment ORDER BY likeCount DESC")
    List<Object[]> findMostLikedComments();

    // Récupérer les commentaires aimés par selon le gameComment id
    @Query("SELECT gcl FROM GameCommentLike gcl WHERE gcl.comment.id = :commentId")
    Optional<GameCommentLike> findByGameCommentId(Integer commentId);
}
