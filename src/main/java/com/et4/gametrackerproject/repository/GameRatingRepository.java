package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameRating;
import com.et4.gametrackerproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GameRatingRepository extends JpaRepository<GameRating,Integer> {


    Optional<GameRating> findByLastModifiedDate(Instant lastModifiedDate);

    // Trouver la note d'un utilisateur pour un jeu spécifique
    Optional<GameRating> findByUserAndGame(User user, Game game);

    // Récupérer toutes les notes pour un jeu spécifique
    List<GameRating> findByGame(Game game);

    // Récupérer toutes les notes d'un utilisateur spécifique
    List<GameRating> findByUser(User user);

    // Récupérer toutes les notes d'un utilisateur avec pagination
    Page<GameRating> findByUser(User user, Pageable pageable);

    // Récupérer toutes les notes pour un jeu avec pagination
    Page<GameRating> findByGame(Game game, Pageable pageable);

    // Trouver les jeux les mieux notés (note moyenne > valeur indiquée)
    @Query("SELECT gr.game, AVG(gr.rating) as avgRating FROM GameRating gr " +
            "GROUP BY gr.game HAVING AVG(gr.rating) >= :minRating " +
            "ORDER BY avgRating DESC")
    List<Object[]> findHighlyRatedGames(@Param("minRating") Double minRating);

    @Query("SELECT gr FROM GameRating gr ORDER BY gr.lastModifiedDate DESC")
    Page<GameRating> findAllByLastModifiedDateDesc(Pageable pageable);

    //AUTRES

    // Compter le nombre de notes pour un jeu
    Long countByGame(Game game);

    // Calculer la note moyenne pour un jeu
    @Query("SELECT AVG(gr.rating) FROM GameRating gr WHERE gr.game = :game")
    Double calculateAverageRatingForGame(@Param("game") Game game);


    @Query("SELECT gr FROM GameRating gr " +
            "WHERE LOWER(gr.game.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
            "   OR LOWER(gr.user.username) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<GameRating> searchRatings(@Param("searchQuery") String searchQuery, Pageable pageable);

    // Récupérer la note d'un jeu id
    @Query("SELECT gr FROM GameRating gr WHERE gr.game.id = :id")
    Optional<GameRating> findByGameId(Integer id);
}
