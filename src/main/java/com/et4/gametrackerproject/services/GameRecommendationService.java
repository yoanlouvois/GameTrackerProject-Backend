package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.GameRecommendationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface GameRecommendationService {

    //Opérations de base
    GameRecommendationDto createRecommendation(Integer senderId, Integer receiverId, Integer gameId, String message);

    GameRecommendationDto updateRecommendationMessage(Integer recommendationId, String newMessage);

    void deleteRecommendationById(Integer recommendationId);

    //Récupération
    GameRecommendationDto getRecommendationById(Integer recommendationId);

    Page<GameRecommendationDto> getRecommendationsBySender(Integer senderId, Pageable pageable);

    Page<GameRecommendationDto> getRecommendationsByReceiver(Integer receiverId, Pageable pageable);

    Page<GameRecommendationDto> getRecommendationsBetweenUsers(Integer user1Id, Integer user2Id, Pageable pageable);


    Long countRecommendationsForGame(Integer gameId);


    // Modération
    Page<GameRecommendationDto> getAllRecommendations(Pageable pageable);

    Map<Integer, Long> getMostRecommendedGames(Pageable pageable);

    Long countRecommendationsReceivedByUser(Integer receiverId);

    Long countRecommendationsSentByUser(Integer senderId);

    Page<GameRecommendationDto> searchRecommendations(String searchQuery, Pageable pageable);

}