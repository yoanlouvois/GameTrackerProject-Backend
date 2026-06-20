package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.FavoriteGame;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteGameRepository extends JpaRepository<FavoriteGame,Integer> {




    // Récupérer tous les jeux favoris d'un utilisateur
    List<FavoriteGame> findByUser(User user);

    // Récupérer tous les utilisateurs qui ont mis un jeu spécifique en favori
    List<FavoriteGame> findByGame(Game game);

    // Trouver les jeux les plus mis en favoris (populaires)
    @Query("SELECT f.game, COUNT(f) as favoriteCount FROM FavoriteGame f GROUP BY f.game ORDER BY favoriteCount DESC")
    List<Object[]> findMostPopularGames();

    // Vérifier si un jeu est favori pour un utilisateur spécifique
    Optional<FavoriteGame> findByUserAndGame(User user, Game game);

    // Récupérer les jeux favoris ajoutés récemment par un utilisateur
    @Query("SELECT f FROM FavoriteGame f WHERE f.user = :user ORDER BY f.creationDate DESC")
    List<FavoriteGame> findRecentlyAddedFavoritesByUser(@Param("user") User user);

    // Trouver les favoris communs entre deux utilisateurs
    @Query("SELECT f1.game FROM FavoriteGame f1, FavoriteGame f2 WHERE f1.user.id = :userId1 AND f2.user.id = :userId2 AND f1.game = f2.game")
    List<Game> findCommonFavoriteGames(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);


    // Compter le nombre d'utilisateurs qui ont mis un jeu en favori
    @Query("SELECT COUNT(f) FROM FavoriteGame f WHERE f.game = :game")
    Long countByGame(@Param("game") Game game);

    // Compter le nombre de jeux favoris pour un utilisateur
    @Query("SELECT COUNT(f) FROM FavoriteGame f WHERE f.user = :user")
    Long countByUser(@Param("user") User user);

    // Vérifier l'existence d'un FavoriteGame pour un utilisateur et un jeu
    boolean existsByUserAndGame(User user, Game game);

    // Supprimer un jeu favori pour un utilisateur spécifique
    void deleteByUserAndGame(User user, Game game);

    // Récupérer un jeu favori par l'id de l'utilisateur
    @Query("SELECT f FROM FavoriteGame f WHERE f.user.id = :userId")
    Optional<FavoriteGame> findFavoriteGameByUserId(Integer userId);

    // Récupérer un jeu favori par l'id du jeu
    @Query("SELECT f FROM FavoriteGame f WHERE f.game.id = :gameId")
    Optional<FavoriteGame> findByGameId(Integer id);
}
