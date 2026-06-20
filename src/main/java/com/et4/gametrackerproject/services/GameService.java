package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.enums.DifficultyLevel;
import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GameService {

    // Opérations de base
    GameDto createGame(GameDto gameDto);
    GameDto updateGame(Integer id, GameDto gameDto);
    void deleteGame(Integer id);
    GameDto getGameById(Integer id);

    // Récupération
    Page<GameDto> getAllGames(Pageable pageable);
    Page<GameDto> searchGames(String query, Pageable pageable);

    //Filtrage
    Page<GameDto> filterByCategory(GameCategory category, Pageable pageable);
    Page<GameDto> filterByDifficulty(DifficultyLevel difficulty, Pageable pageable);
    Page<GameDto> filterByAgeRange(Integer minAge, Integer maxAge, Pageable pageable);
    Page<GameDto> filterByTags(Set<String> tags, Pageable pageable);

    // Recherche d'un jeu par son URL
    Optional<Game> getGameByUrl(String url);

    String getImageUrlById(Integer id);

    // Recherche des jeux par nom exact
    List<Game> getGamesByName(String name);

    // Recherche des jeux actifs avec pagination
    Page<Game> getGamesByIsActive(Boolean isActive, Pageable pageable);

    // Recherche des jeux par catégorie et niveau de difficulté
    List<Game> getGamesByCategoryAndDifficulty(GameCategory category, DifficultyLevel difficultyLevel);

    // Recherche des jeux ayant une note supérieure ou égale à minRating
    List<Game> getHighlyRatedGames(Double minRating);

    // Recherche des jeux les plus populaires (triés par playCount décroissant)
    List<Game> getMostPopularGames(Pageable pageable);

    // Recherche des jeux accessibles pour un âge donné (minAge <= age)
    List<Game> getGamesByMinAgeLessThanEqual(Integer age);

    // Recherche combinée avec plusieurs filtres
    Page<Game> getGamesWithFilters(String name, GameCategory category, DifficultyLevel difficulty,
                                   Double minRating, Integer minAge, Pageable pageable);

    // Recherche des jeux les plus récents
    List<Game> getNewestGames(Pageable pageable);

    // Recherche des jeux populaires par catégorie
    List<Game> getMostPopularGamesByCategory(GameCategory category, Pageable pageable);
}