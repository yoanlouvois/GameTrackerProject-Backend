package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.DailyGameSessionApi;
import com.et4.gametrackerproject.dto.DailyGameSessionDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.services.DailyGameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
public class DailyGameSessionController implements DailyGameSessionApi {

    @Autowired
    private DailyGameSessionService dailyGameSessionService;

    public DailyGameSessionController(DailyGameSessionService dailygamesessionservice) {
        this.dailyGameSessionService = dailygamesessionservice;
    }


    @Override
    public DailyGameSessionDto getSessionById(Integer id) {
        return dailyGameSessionService.getSessionById(id);
    }

    @Override
    public DailyGameSessionDto createSession(DailyGameSessionDto sessionDto) {
        return dailyGameSessionService.createSession(sessionDto);
    }

    @Override
    public DailyGameSessionDto updateSession(Integer id, DailyGameSessionDto sessionDto) {
        return dailyGameSessionService.updateSession(id, sessionDto);
    }

    @Override
    public void deleteSession(Integer id) {
        dailyGameSessionService.deleteSession(id);
    }

    @Override
    public DailyGameSessionDto getSessionByUserAndDate(Integer userId, Instant date) {
        return dailyGameSessionService.getSessionByUserAndDate(userId, date);
    }

    @Override
    public List<DailyGameSessionDto> getSessionsForUser(Integer userId) {
        return dailyGameSessionService.getSessionsForUser(userId);
    }

    @Override
    public List<DailyGameSessionDto> getSessionsForUserBetweenDates(Integer userId, Instant start, Instant end) {
        return dailyGameSessionService.getSessionsForUserBetweenDates(userId, start, end);
    }


    @Override
    public Instant getLastPlayedDate(Integer userId) {
        return dailyGameSessionService.getLastPlayedDate(userId);
    }

    @Override
    public List<DailyGameSessionDto> getSessionByDate(Instant date) {
        return dailyGameSessionService.getSessionByDate(date);
    }

    @Override
    public Map<UserDto, Long> getMostActiveUsers() {
        return dailyGameSessionService.getMostActiveUsers();
    }

    @Override
    public Integer calculateTotalPlaytimeByUser(Integer userId) {
        return dailyGameSessionService.calculateTotalPlaytimeByUser(userId);
    }

    @Override
    public Integer calculatePlaytimeByUserInPeriod(Integer userId, Instant startDate, Instant endDate) {
        return dailyGameSessionService.calculatePlaytimeByUserInPeriod(userId, startDate, endDate);
    }

    @Override
    public Long countSessionsByUser(Integer userId) {
        return dailyGameSessionService.countSessionsByUser(userId);
    }

    @Override
    public Integer countGamesPlayedByUser(Integer userId) {
        return dailyGameSessionService.countGamesPlayedByUser(userId);
    }

    @Override
    public DailyGameSessionDto getLongestSessionForUser(Integer userId) {
        return dailyGameSessionService.getLongestSessionForUser(userId);
    }

    @Override
    public List<DailyGameSessionDto> getRecentSessionsForUser(Integer userId, int limit) {
        return dailyGameSessionService.getRecentSessionsForUser(userId, limit);
    }

    @Override
    public Double calculateAveragePlaytimeByUser(Integer userId) {
        return dailyGameSessionService.calculateAveragePlaytimeByUser(userId);
    }


}
