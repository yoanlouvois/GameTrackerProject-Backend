package com.et4.gametrackerproject.controller;


import com.et4.gametrackerproject.dto.GameRatingDto;
import com.et4.gametrackerproject.services.GameRatingService;
import com.et4.gametrackerproject.controller.api.GameRatingApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;


@RestController
public class GameRatingController implements GameRatingApi {

    @Autowired
    private GameRatingService gameRatingService;

    public GameRatingController(GameRatingService gameRatingService) {
        this.gameRatingService = gameRatingService;
    }


    @Override
    public GameRatingDto submitRating(GameRatingDto ratingDto) {
        return gameRatingService.submitRating(ratingDto);
    }

    @Override
    public GameRatingDto updateRating(Integer ratingId, Integer newRating) {
        return gameRatingService.updateRating(ratingId, newRating);
    }

    @Override
    public void deleteRating(Integer ratingId) {
        gameRatingService.deleteRatingById(ratingId);
    }

    @Override
    public GameRatingDto getRatingById(Integer ratingId) {
        return gameRatingService.getRatingById(ratingId);
    }

    @Override
    public GameRatingDto getUserRatingForGame(Integer userId, Integer gameId) {
        return gameRatingService.getUserRatingForGame(userId, gameId);
    }

    @Override
    public Page<GameRatingDto> getRatingsForGame(Integer gameId, Pageable pageable) {
        return gameRatingService.getRatingsForGame(gameId, pageable);
    }

    @Override
    public Double calculateAverageRatingForGame(Integer gameId) {
        return gameRatingService.calculateAverageRatingForGame(gameId);
    }

    @Override
    public Long countRatingsForGame(Integer gameId) {
        return gameRatingService.countRatingsForGame(gameId);
    }

    @Override
    public Page<GameRatingDto> getRecentRatings(Pageable pageable) {
        return gameRatingService.getRecentRatings(pageable);
    }

    @Override
    public Page<GameRatingDto> searchRatings(String searchQuery, Pageable pageable) {
        return gameRatingService.searchRatings(searchQuery, pageable);
    }

    @Override
    public Map<Integer, Long> getTopRatedGames(int limit) {
        return gameRatingService.getTopRatedGames(limit);
    }

    @Override
    public GameRatingDto getRatingByDate(Instant date) {
        return gameRatingService.getRatingByDate(date);
    }
}
