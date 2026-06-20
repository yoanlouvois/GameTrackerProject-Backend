package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.GameProgressApi;
import com.et4.gametrackerproject.dto.GameProgressDto;
import com.et4.gametrackerproject.enums.GameStatus;
import com.et4.gametrackerproject.services.GameProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GameProgressController implements GameProgressApi {

    @Autowired
    private GameProgressService gameProgressService;

    GameProgressController(GameProgressService gameProgressService) {
        this.gameProgressService = gameProgressService;
    }


    @Override
    public GameProgressDto createProgress(GameProgressDto progressDto) {
        return gameProgressService.createOrUpdateProgress(progressDto);
    }

    @Override
    public GameProgressDto getProgressById(Integer progressId) {
        return gameProgressService.getProgressById(progressId);
    }

    @Override
    public void deleteProgress(Integer progressId) {
        gameProgressService.deleteGameProgressById(progressId);
    }

    @Override
    public GameProgressDto startNewGameSession(Integer userId, Integer gameId) {
        return gameProgressService.startNewGameSession(userId, gameId);
    }

    @Override
    public GameProgressDto updateGameplaySession(Integer progressId, Integer scoreDelta, Integer timeDelta) {
        return gameProgressService.updateGameplaySession(progressId, scoreDelta, timeDelta);
    }

    @Override
    public GameProgressDto completeGame(Integer progressId) {
        return gameProgressService.completeGame(progressId);
    }

    @Override
    public GameProgressDto resetProgress(Integer progressId) {
        return gameProgressService.resetProgress(progressId);
    }

    @Override
    public GameProgressDto recordAttempt(Integer progressId, boolean won) {
        return gameProgressService.recordAttempt(progressId, won);
    }

    @Override
    public GameProgressDto updateBestScore(Integer progressId, Integer newScore) {
        return gameProgressService.updateBestScore(progressId, newScore);
    }

    @Override
    public GameProgressDto incrementStreak(Integer progressId) {
        return gameProgressService.incrementStreak(progressId);
    }

    @Override
    public GameProgressDto resetStreak(Integer progressId) {
        return gameProgressService.resetStreak(progressId);
    }

    @Override
    public GameProgressDto getCurrentProgress(Integer userId, Integer gameId) {
        return gameProgressService.getCurrentProgress(userId, gameId);
    }

    @Override
    public List<GameProgressDto> getAllUserProgress(Integer userId) {
        return gameProgressService.getAllUserProgress(userId);
    }

    @Override
    public List<GameProgressDto> getGamesByStatus(Integer userId, GameStatus status) {
        return gameProgressService.getGamesByStatus(userId, status);
    }

    @Override
    public List<GameProgressDto> getProgressForGame(Integer gameId) {
        return gameProgressService.getProgressForGame(gameId);
    }

    @Override
    public List<GameProgressDto> getProgressByUserOrderByBestScoreDesc(Integer userId) {
        return gameProgressService.getProgressByUserOrderByBestScoreDesc(userId);
    }

    @Override
    public List<GameProgressDto> getProgressByUserOrderByTimePlayedDesc(Integer userId) {
        return gameProgressService.getProgressByUserOrderByTimePlayedDesc(userId);
    }

    @Override
    public List<GameProgressDto> getRecentlyPlayedGames(Integer userId, int hours) {
        return gameProgressService.getRecentlyPlayedGames(userId, hours);
    }

    @Override
    public Map<GameStatus, Long> countGamesByStatusForUser(Integer userId) {
        return gameProgressService.countGamesByStatusForUser(userId);
    }

    @Override
    public Integer getTotalPlaytimeForUser(Integer userId) {
        return gameProgressService.getTotalPlaytimeForUser(userId);
    }

    @Override
    public Map<Integer, Integer> getUsersByGameOrderedByPlaytime(Integer gameId) {
        return gameProgressService.getUsersByGameOrderedByPlaytime(gameId);
    }

    @Override
    public Map<Integer, Long> getMostPopularGames() {
        return gameProgressService.getMostPopularGames();
    }

    @Override
    public Map<Integer, Integer> getTopScoringUsersForGame(Integer gameId) {
        return gameProgressService.getTopScoringUsersForGame(gameId);
    }

    @Override
    public List<Map<String, Object>> getUsersWithLongestStreaks() {
        return gameProgressService.getUsersWithLongestStreaks();
    }
}
