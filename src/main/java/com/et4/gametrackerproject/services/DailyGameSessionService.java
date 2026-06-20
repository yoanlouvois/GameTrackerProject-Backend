package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.DailyGameSessionDto;
import com.et4.gametrackerproject.dto.UserDto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface DailyGameSessionService {

    //Opérations de base
    DailyGameSessionDto getSessionById(Integer id);
    DailyGameSessionDto createSession(DailyGameSessionDto sessionDto);
    DailyGameSessionDto updateSession(Integer id, DailyGameSessionDto sessionDto);
    void deleteSession(Integer id);



    DailyGameSessionDto getSessionByUserAndDate(Integer userId, Instant date);

    List<DailyGameSessionDto> getSessionsForUser(Integer userId);

    List<DailyGameSessionDto> getSessionsForUserBetweenDates(Integer userId, Instant start, Instant end);


    List<DailyGameSessionDto> getSessionByDate(Instant date);


    Map<UserDto, Long> getMostActiveUsers();

    Integer calculateTotalPlaytimeByUser(Integer userId);

    Integer calculatePlaytimeByUserInPeriod(Integer userId, Instant startDate, Instant endDate);

    Long countSessionsByUser(Integer userId);

    Integer countGamesPlayedByUser(Integer userId);

    DailyGameSessionDto getLongestSessionForUser(Integer userId);

    List<DailyGameSessionDto> getRecentSessionsForUser(Integer userId, int limit);

    Double calculateAveragePlaytimeByUser(Integer userId);

    //Utilitaires
    Instant getLastPlayedDate(Integer userId);

}