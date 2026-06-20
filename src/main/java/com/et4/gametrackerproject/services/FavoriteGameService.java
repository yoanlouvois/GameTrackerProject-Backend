package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.FavoriteGameDto;
import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FavoriteGameService {

    //Opérations de base
    FavoriteGameDto addToFavorites(FavoriteGameDto favoriteDto);
    void deleteById(Integer favoriteId);

    //Récupération
    List<GameDto> getFavoriteGamesForUser(Integer userId);
    List<UserDto> getUsersWhoFavoritedGame(Integer gameId);
    boolean isGameFavoritedByUser(Integer userId, Integer gameId);
    FavoriteGameDto getFavoriteById(Integer favoriteId);

    //Statistiques
    Long getTotalFavoritesCountForGame(Integer gameId);
    Map<String, Long> getMostFavoritedGames(int limit);
    Map<Integer, Long> getFavoriteCountByGameCategory();

    //Vérifier si un jeu est favori pour un utilisateur spécifique
    Optional<FavoriteGameDto> findFavoriteByUserAndGame(Integer userId, Integer gameId);

    //Récupérer les jeux favoris ajoutés récemment par un utilisateur
    List<FavoriteGameDto> getRecentlyAddedFavoritesForUser(Integer userId);

    // 3. Trouver les favoris communs entre deux utilisateurs
    List<GameDto> getCommonFavoriteGames(Integer userId1, Integer userId2);

    //Compter le nombre de jeux favoris pour un utilisateur
    Long countFavoritesByUser(Integer userId);

}