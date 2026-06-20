package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.GameLeaderboardDto;
import com.et4.gametrackerproject.enums.LeaderboardPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GameLeaderboardService {

    //Opérations de base
    GameLeaderboardDto submitScore(GameLeaderboardDto scoreEntry);
    GameLeaderboardDto updateScore(Integer entryId, Integer newScore);
    void deleteGameLeaderBoardById(Integer entryId);

    //Récupération des classements
    Page<GameLeaderboardDto> getLeaderboardForGame(Integer gameId, LeaderboardPeriod period, Pageable pageable);


    Page<GameLeaderboardDto> getLeaderboardByPeriod(LeaderboardPeriod period, Pageable pageable);


    Optional<GameLeaderboardDto> getLeaderBoardByGameUserPeriod(Integer gameId, Integer userId, LeaderboardPeriod period);

    List<GameLeaderboardDto> getLeaderboardByGamePeriodScore(Integer gameId, LeaderboardPeriod period);

    Page<GameLeaderboardDto> getLeaderboardPageByRank(Integer gameId, LeaderboardPeriod period, Pageable pageable);

    List<GameLeaderboardDto> getTopRankedPlayersByGamePeriod(Integer gameId, LeaderboardPeriod period, int limit);


    List<GameLeaderboardDto> getLeaderboardEntriesForUserAndGame(Integer userId, Integer gameId);

    List<GameLeaderboardDto> getLeaderboardEntriesByDate(Instant date);
}