package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.enums.DifficultyLevel;
import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameRepository extends JpaRepository<Game,Integer> {

    // Recherches de base
    Optional<Game> findByUrl(String url);

    // Récupération de l'URL de l'image d'un jeu
    @Query("SELECT g.imageUrl FROM Game g WHERE g.id = :id")
    String findImageUrlById(@Param("id") Integer id);

    List<Game> findByName(String name);

    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Game> findByNameContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    Page<Game> findByCategory(GameCategory category,Pageable pageable);

    Page<Game> findByDifficultyLevel(DifficultyLevel difficultyLevel,Pageable pageable);

    // Recherche des jeux avec des tags spécifiques
    @Query("SELECT DISTINCT g FROM Game g " +
            "JOIN g.tags t " +
            "WHERE t.tag.name IN :tagNames")
    Page<Game> findGamesByTags(@Param("tagNames") Set<String> tagNames, Pageable pageable);

    // Nouvelle méthode : recherche par tranche d'âge
    @Query("SELECT g FROM Game g WHERE g.minAge BETWEEN :minAge AND :maxAge")
    Page<Game> findByMinAgeBetween(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge, Pageable pageable);


    Page<Game> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.category = :category AND g.difficultyLevel = :difficultyLevel")
    List<Game> findByCategoryAndDifficultyLevel(@Param("category") GameCategory category,
                                                @Param("difficultyLevel") DifficultyLevel difficultyLevel);

    @Query("SELECT g FROM Game g WHERE g.averageRating >= :minRating")
    List<Game> findHighlyRatedGames(@Param("minRating") Double minRating);

    @Query("SELECT g FROM Game g ORDER BY g.playCount DESC")
    List<Game> findMostPopularGames(Pageable pageable);

    // Recherche par restriction d'âge
    List<Game> findByMinAgeLessThan(Integer age);

    // Combinaisons de critères complexes
    @Query("SELECT g FROM Game g WHERE " +
            "(:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR g.category = :category) AND " +
            "(:difficulty IS NULL OR g.difficultyLevel = :difficulty) AND " +
            "(:minRating IS NULL OR g.averageRating >= :minRating) AND " +
            "(:minAge IS NULL OR g.minAge <= :minAge) AND " +
            "g.isActive = true")
    Page<Game> findGamesWithFilters(
            @Param("name") String name,
            @Param("category") GameCategory category,
            @Param("difficulty") DifficultyLevel difficulty,
            @Param("minRating") Double minRating,
            @Param("minAge") Integer minAge,
            Pageable pageable);


    // Recherches des jeux les plus récents
    @Query("SELECT g FROM Game g ORDER BY g.creationDate DESC")
    List<Game> findNewestGames(Pageable pageable);
    // Recherche des jeux populaires par catégorie
    @Query("SELECT g FROM Game g " +
            "WHERE g.category = :category " +
            "ORDER BY g.playCount DESC")
    List<Game> findMostPopularGamesByCategory(@Param("category") GameCategory category, Pageable pageable);

    // Recherche des jeux par ID favoris
    @Query("SELECT g FROM Game g " +
            "JOIN FavoriteGame f ON g.id = f.game.id " +
            "WHERE f.id = :favoriteId")
    Optional<Game> findByFavoriteId(Integer favoriteId);

    // Recherche des jeux par ID de GameComment
    @Query("SELECT g FROM Game g " +
            "JOIN GameComment gc ON g.id = gc.game.id " +
            "WHERE gc.id = :commentId")
    Optional<Game> findByGameCommentId(Integer commentId);


    // Recherche des jeux par ID de GameRecommendationId
    @Query("SELECT g FROM Game g " +
            "JOIN GameRecommendation gr ON g.id = gr.game.id " +
            "WHERE gr.id = :recommendationId")
    Optional<Game> findByGameLeaderboardId(Integer entryId);

    // Recherche des jeux par ID de GameProgressId
    @Query("SELECT g FROM Game g " +
            "JOIN GameProgress gp ON g.id = gp.game.id " +
            "WHERE gp.id = :progressId")
    Optional<Game> findByGameProgressId(Integer progressId);

    // Recherche des jeux par ID de GameRating
    @Query("SELECT g FROM Game g " +
            "JOIN GameRating gr ON g.id = gr.game.id " +
            "WHERE gr.id = :ratingId")
    Optional<Game> findByGameRatingId(Integer ratingId);

    // Recherche des jeux par ID de GameRecommendationId
    @Query("SELECT g FROM Game g " +
            "JOIN GameRecommendation gr ON g.id = gr.game.id " +
            "WHERE gr.id = :recommendationId")
    Optional<Game> findByGameRecommendationId(Integer recommendationId);

    // Recherche des jeux par ID de Game
    @Query("SELECT g FROM Game g " +
            "JOIN GameTag gt ON g.id = gt.game.id " +
            "WHERE gt.id = :tagId")
    Optional<GameComment> findByGameId(Integer id);

    //trouver le jeu par GameTagId
    @Query("SELECT g FROM Game g JOIN g.tags gt WHERE gt.id = :gameTagId")
    Optional<Game> findByGameTagId(@Param("gameTagId") Integer gameTagId);
}
