package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameComment;
import com.et4.gametrackerproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GameCommentRepository extends JpaRepository<GameComment,Integer> {

    // Récupérer tous les commentaires pour un jeu spécifique
    List<GameComment> findByGame(Game game);

    // Récupérer tous les commentaires d'un utilisateur
    Page<GameComment> findByUser(User user, Pageable pageable);

    // Récupérer tous les commentaires en réponse à un commentaire parent
    List<GameComment> findByParentComment(GameComment parentComment);

    // Récupérer tous les commentaires en réponse à un commentaire parent
    Page<GameComment> findByParentComment(GameComment parentComment, Pageable pageable);

    // Pagination pour les commentaires d'un jeu
    Page<GameComment> findByGame(Game game, Pageable pageable);

    // Récupérer tous les commentaires créés après une certaine date
    @Query("SELECT gc FROM GameComment gc WHERE gc.creationDate > :date")
    List<GameComment> findByCreationDateAfter(@Param("date") Instant date);

    // Rechercher des commentaires dont le contenu contient un terme précis (insensible à la casse) avec pagination
    @Query("SELECT gc FROM GameComment gc WHERE LOWER(gc.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<GameComment> findByContentContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Récupérer les commentaires signalés (en joignant la table Report, où le type est 'comment')
    @Query(value = "SELECT gc.* FROM gamecomment gc " +
            "JOIN report r ON r.content_id = gc.id " +
            "WHERE r.type = 'comment'", nativeQuery = true)
    Page<GameComment> findReportedComments(Pageable pageable);

    // Récupérer l'ID du jeu et le nombre de commentaires, trié par le nombre de commentaires décroissant
    @Query("SELECT gc.game.id, COUNT(gc) FROM GameComment gc GROUP BY gc.game.id ORDER BY COUNT(gc) DESC")
    List<Object[]> findTopCommentedGames();




    // Compter le nombre de commentaires pour un jeu
    @Query("SELECT COUNT(gc) FROM GameComment gc WHERE gc.game = :game")
    Long countByGame(@Param("game") Game game);

    // Compter le nombre de commentaires par un utilisateur
    @Query("SELECT COUNT(gc) FROM GameComment gc WHERE gc.user = :user")
    Long countByUser(@Param("user") User user);

    // Compter le nombre de réponses à un commentaire parent
    @Query("SELECT COUNT(gc) FROM GameComment gc WHERE gc.parentComment = :parentComment")
    Long countReplies(@Param("parentComment") GameComment parentComment);

    @Query("SELECT gc FROM GameComment gc WHERE gc.game = :game ORDER BY gc.creationDate DESC")
    List<GameComment> findByGameOrderByCreationDateDesc(@Param("game") Game game);


    // Trouver les commentaires les plus populaires (plus de likes)
    @Query("SELECT gc, COUNT(gcl) AS likeCount FROM GameComment gc LEFT JOIN gc.likes gcl " +
            "WHERE gc.game = :game GROUP BY gc ORDER BY likeCount DESC")
    List<Object[]> findMostLikedCommentsByGame(@Param("game") Game game);

    // Trouver les commentaires avec le plus de réponses
    @Query("SELECT gc, COUNT(gr) AS replyCount FROM GameComment gc LEFT JOIN gc.replies gr WHERE gc.game = :game GROUP BY gc ORDER BY replyCount DESC")
    List<Object[]> findMostDiscussedCommentsByGame(@Param("game") Game game);


    // Trouver les commentaires selon GameCommentLike Id
    @Query("SELECT gc FROM GameComment gc JOIN gc.likes gcl WHERE gcl.id = :likeId")
    Optional<GameComment> findByGameCommentLikeId(Integer likeId);

    // Trouver les commentaires selon Game Id
    @Query("SELECT gc FROM GameComment gc WHERE gc.game.id = :gameId")
    Optional<GameComment> findByGameId(Integer id);
}
