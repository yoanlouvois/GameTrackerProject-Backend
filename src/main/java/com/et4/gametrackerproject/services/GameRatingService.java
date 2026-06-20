package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.GameRatingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Map;

public interface GameRatingService {

    //Opérations de base
    GameRatingDto submitRating(GameRatingDto ratingDto);
    GameRatingDto updateRating(Integer ratingId, Integer newRating);
    void deleteRatingById(Integer ratingId);

    //Récupération des évaluations
    GameRatingDto getRatingById(Integer ratingId);

    GameRatingDto getRatingByDate(Instant date);

    GameRatingDto getUserRatingForGame(Integer userId, Integer gameId);
    Page<GameRatingDto> getRatingsForGame(Integer gameId, Pageable pageable);

    //Statistiques
    Double calculateAverageRatingForGame(Integer gameId);
    Long countRatingsForGame(Integer gameId);

    //Modération
    Page<GameRatingDto> getRecentRatings(Pageable pageable);

    Page<GameRatingDto> searchRatings(String searchQuery, Pageable pageable);

    //Analyse
    Map<Integer, Long> getTopRatedGames(int limit);

}