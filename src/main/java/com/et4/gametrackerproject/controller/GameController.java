package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.GameApi;
import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.enums.DifficultyLevel;
import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.Tag;
import com.et4.gametrackerproject.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
public class GameController implements GameApi {

    @Autowired
    private GameService gameService;

    GameController (GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public GameDto createGame(GameDto gameDto) {
        return gameService.createGame(gameDto);
    }

    @Override
    public GameDto updateGame(Integer id, GameDto gameDto) {
        return gameService.updateGame(id, gameDto);
    }

    @Override
    public void deleteGame(Integer id) {
        gameService.deleteGame(id);
    }

    @Override
    public GameDto getGameById(Integer id) {
        return gameService.getGameById(id);
    }

    @Override
    public Page<GameDto> getAllGames(Pageable pageable) {
        return gameService.getAllGames(pageable);
    }

    @Override
    public Page<GameDto> searchGames(String query, Pageable pageable) {
        return gameService.searchGames(query, pageable);
    }

    @Override
    public Page<GameDto> filterByCategory(GameCategory category, Pageable pageable) {
        return gameService.filterByCategory(category, pageable);
    }

    @Override
    public Page<GameDto> filterByDifficulty(DifficultyLevel difficulty, Pageable pageable) {
        return gameService.filterByDifficulty(difficulty, pageable);
    }

    @Override
    public Page<GameDto> filterByAgeRange(Integer minAge, Integer maxAge, Pageable pageable) {
        return gameService.filterByAgeRange(minAge, maxAge, pageable);
    }

    @Override
    public Page<GameDto> filterByTags(Set<String> tags, Pageable pageable) {
        return gameService.filterByTags(tags, pageable);
    }

    @Override
    public Optional<Game> getGameByUrl(String url) {
        return gameService.getGameByUrl(url);
    }

    @Override
    public String getImageUrlByID(Integer id) {
        return gameService.getImageUrlById(id);
    }

    @Override
    public List<Game> getGamesByName(String name) {
        return gameService.getGamesByName(name);
    }

    @Override
    public Page<Game> getGamesByIsActive(Boolean isActive, Pageable pageable) {
        return gameService.getGamesByIsActive(isActive, pageable);
    }

    @Override
    public List<Game> getGamesByCategoryAndDifficulty(GameCategory category, DifficultyLevel difficultyLevel) {
        return gameService.getGamesByCategoryAndDifficulty(category, difficultyLevel);
    }

    @Override
    public List<Game> getHighlyRatedGames(Double minRating) {
        return gameService.getHighlyRatedGames(minRating);
    }

    @Override
    public List<Game> getMostPopularGames(Pageable pageable) {
        return gameService.getMostPopularGames(pageable);
    }

    @Override
    public List<Game> getGamesByMinAgeLessThanEqual(Integer age) {
        return gameService.getGamesByMinAgeLessThanEqual(age);
    }

    @Override
    public Page<Game> getGamesWithFilters(String name, GameCategory category, DifficultyLevel difficulty, Double minRating, Integer minAge, Pageable pageable) {
        return gameService.getGamesWithFilters(name, category, difficulty, minRating, minAge, pageable);
    }

    @Override
    public List<Game> getNewestGames(Pageable pageable) {
        return gameService.getNewestGames(pageable);
    }

    @Override
    public List<Game> getMostPopularGamesByCategory(GameCategory category, Pageable pageable) {
        return gameService.getMostPopularGamesByCategory(category, pageable);
    }
}
