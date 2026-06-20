package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.GameLeaderboardApi;
import com.et4.gametrackerproject.dto.GameLeaderboardDto;
import com.et4.gametrackerproject.enums.LeaderboardPeriod;
import com.et4.gametrackerproject.services.GameLeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
public class GameLeaderboardController implements GameLeaderboardApi {

    @Autowired
    private GameLeaderboardService gameLeaderboardService;

    public GameLeaderboardController(GameLeaderboardService gameLeaderboardService) {
        this.gameLeaderboardService = gameLeaderboardService;
    }


    @Override
    public GameLeaderboardDto submitScore(GameLeaderboardDto scoreEntry) {
        return gameLeaderboardService.submitScore(scoreEntry);
    }

    @Override
    public GameLeaderboardDto updateScore(Integer entryId, Integer newScore) {
        return gameLeaderboardService.updateScore(entryId, newScore);
    }

    @Override
    public void deleteScoreEntry(Integer entryId) {
        gameLeaderboardService.deleteGameLeaderBoardById(entryId);
    }

    @Override
    public Page<GameLeaderboardDto> getLeaderboardForGame(Integer gameId, LeaderboardPeriod period, Pageable pageable) {
        return gameLeaderboardService.getLeaderboardForGame(gameId, period, pageable);
    }

    @Override
    public Page<GameLeaderboardDto> getLeaderboardByPeriod(LeaderboardPeriod period, Pageable pageable) {
        return gameLeaderboardService.getLeaderboardByPeriod(period, pageable);
    }

    @Override
    public Optional<GameLeaderboardDto> getLeaderBoardByGameUserPeriod(Integer gameId, Integer userId, LeaderboardPeriod period) {
        return gameLeaderboardService.getLeaderBoardByGameUserPeriod(gameId, userId, period);
    }

    @Override
    public List<GameLeaderboardDto> getLeaderboardByGamePeriodScore(Integer gameId, LeaderboardPeriod period) {
        return gameLeaderboardService.getLeaderboardByGamePeriodScore(gameId, period);
    }

    @Override
    public Page<GameLeaderboardDto> getLeaderboardPageByRank(Integer gameId, LeaderboardPeriod period, Pageable pageable) {
        return gameLeaderboardService.getLeaderboardPageByRank(gameId, period, pageable);
    }

    @Override
    public List<GameLeaderboardDto> getTopRankedPlayersByGamePeriod(Integer gameId, LeaderboardPeriod period, int limit) {
        return gameLeaderboardService.getTopRankedPlayersByGamePeriod(gameId, period, limit);
    }

    @Override
    public List<GameLeaderboardDto> getLeaderboardEntriesForUserAndGame(Integer userId, Integer gameId) {
        return gameLeaderboardService.getLeaderboardEntriesForUserAndGame(userId, gameId);
    }

    @Override
    public List<GameLeaderboardDto> getLeaderboardEntriesByDate(Instant date) {
        return gameLeaderboardService.getLeaderboardEntriesByDate(date);
    }
}
