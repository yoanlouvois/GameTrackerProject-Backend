package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameRecommendation;
import com.et4.gametrackerproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRecommendationRepository extends JpaRepository<GameRecommendation,Integer>{
    
    // Rechercher les recommandations envoyées par un utilisateur avec pagination
    Page<GameRecommendation> findBySender(User sender, Pageable pageable);

    // Rechercher les recommandations reçues par un utilisateur avec pagination
    Page<GameRecommendation> findByReceiver(User receiver, Pageable pageable);

    // Rechercher les recommandations pour un jeu spécifique
    List<GameRecommendation> findByGame(Game game);

    // Rechercher les recommandations pour un jeu spécifique avec pagination
    Page<GameRecommendation> findByGame(Game game, Pageable pageable);

    // Trouver les recommandations d'un utilisateur à un autre
    @Query("SELECT gr FROM GameRecommendation gr " +
            "WHERE (gr.sender = :sender AND gr.receiver = :receiver) " +
            "   OR (gr.sender = :receiver AND gr.receiver = :sender)")
    Page<GameRecommendation> findBySenderAndReceiver(@Param("sender") User sender,
                                                     @Param("receiver") User receiver,
                                                     Pageable pageable);

    // Trouver les recommandations d'un utilisateur à un autre
    @Query("SELECT gr FROM GameRecommendation gr " +
            "WHERE (gr.sender = :sender AND gr.receiver = :receiver) " +
            "   OR (gr.sender = :receiver AND gr.receiver = :sender)")
    List<GameRecommendation> findBySenderAndReceiver(@Param("sender") User sender,
                                                     @Param("receiver") User receiver);

    @Query("SELECT gr.game.id, COUNT(gr) FROM GameRecommendation gr " +
            "GROUP BY gr.game.id ORDER BY COUNT(gr) DESC")
    List<Object[]> findMostRecommendedGames(Pageable pageable);

    // Compter le nombre de recommandations reçues par un utilisateur
    Long countByReceiver(User receiver);

    // Compter le nombre de recommandations envoyées par un utilisateur
    Long countBySender(User sender);

    Long countByGame(Game game);

    @Query("SELECT gr FROM GameRecommendation gr WHERE LOWER(gr.message) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<GameRecommendation> findByMessageContainingIgnoreCase(@Param("searchQuery") String searchQuery, Pageable pageable);

    // Rechercher une recommandation par son userId
    @Query("SELECT gr FROM GameRecommendation gr WHERE gr.sender.id = :userId OR gr.receiver.id = :userId")
    Optional<GameRecommendation> findByUserId(Integer userId);

    // Rechercher une recommandation par son gameId
    @Query("SELECT gr FROM GameRecommendation gr WHERE gr.game.id = :gameId")
    Optional<GameRecommendation> findByGameId(Integer id);
}
