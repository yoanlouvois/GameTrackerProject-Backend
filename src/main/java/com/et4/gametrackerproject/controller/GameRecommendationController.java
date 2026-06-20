package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.GameRecommendationApi;
import com.et4.gametrackerproject.dto.GameRecommendationDto;
import com.et4.gametrackerproject.services.GameRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GameRecommendationController implements GameRecommendationApi {

    @Autowired
    private GameRecommendationService gameRecommendationService;

    public GameRecommendationController(GameRecommendationService gameRecommendationService) {
        this.gameRecommendationService = gameRecommendationService;
    }


    @Override
    public GameRecommendationDto createRecommendation(Integer senderId, Integer receiverId, Integer gameId, String message) {
        return gameRecommendationService.createRecommendation(senderId, receiverId, gameId, message);
    }

    @Override
    public GameRecommendationDto updateRecommendationMessage(Integer recommendationId, String newMessage) {
        return gameRecommendationService.updateRecommendationMessage(recommendationId, newMessage);
    }

    @Override
    public void deleteRecommendation(Integer recommendationId) {
        gameRecommendationService.deleteRecommendationById(recommendationId);
    }

    @Override
    public GameRecommendationDto getRecommendationById(Integer recommendationId) {
        return gameRecommendationService.getRecommendationById(recommendationId);
    }

    @Override
    public Page<GameRecommendationDto> getRecommendationsBySender(Integer senderId, Pageable pageable) {
        return gameRecommendationService.getRecommendationsBySender(senderId, pageable);
    }

    @Override
    public Page<GameRecommendationDto> getRecommendationsByReceiver(Integer receiverId, Pageable pageable) {
        return gameRecommendationService.getRecommendationsByReceiver(receiverId, pageable);
    }

    @Override
    public Page<GameRecommendationDto> getRecommendationsBetweenUsers(Integer user1Id, Integer user2Id, Pageable pageable) {
        return gameRecommendationService.getRecommendationsBetweenUsers(user1Id, user2Id, pageable);
    }

    @Override
    public Long countRecommendationsForGame(Integer gameId) {
        return gameRecommendationService.countRecommendationsForGame(gameId);
    }

    @Override
    public Page<GameRecommendationDto> getAllRecommendations(Pageable pageable) {
        return gameRecommendationService.getAllRecommendations(pageable);
    }

    @Override
    public Page<GameRecommendationDto> searchRecommendations(String searchQuery, Pageable pageable) {
        return gameRecommendationService.searchRecommendations(searchQuery, pageable);
    }
    @Override
    public Map<Integer, Long> getMostRecommendedGames(Pageable pageable) {
        return gameRecommendationService.getMostRecommendedGames(pageable);
    }

    @Override
    public Long countRecommendationsReceivedByUser(Integer receiverId) {
        return gameRecommendationService.countRecommendationsReceivedByUser(receiverId);
    }

    @Override
    public Long countRecommendationsSentByUser(Integer senderId) {
        return gameRecommendationService.countRecommendationsSentByUser(senderId);
    }
}
