package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.LeaderboardPeriod;
import com.et4.gametrackerproject.model.GameLeaderboard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameLeaderboardDto {
    private Integer id;

    private Instant lastModifiedDate;

    @JsonIgnore
    private GameDto game;

    private UserDto user;

    private Integer score;

    private Integer rankNumber;

    private Instant date;

    private LeaderboardPeriod period;

    public static GameLeaderboardDto fromEntity(GameLeaderboard gameLeaderboard) {
        if(gameLeaderboard == null) {
            return null;
            //TODO: throw exception
        }

        return GameLeaderboardDto.builder()
                .id(gameLeaderboard.getId())
                .lastModifiedDate(gameLeaderboard.getLastModifiedDate())
                .user(UserDto.fromEntity(gameLeaderboard.getUser()))
                .score(gameLeaderboard.getScore())
                .rankNumber(gameLeaderboard.getRankNumber())
                .date(gameLeaderboard.getDate())
                .period(gameLeaderboard.getPeriod())
                .build();
    }

    public static GameLeaderboard toEntity(GameLeaderboardDto gameLeaderboardDto) {
        if (gameLeaderboardDto == null) {
            return null;
            //TODO: throw exception
        }

        return GameLeaderboard.builder()
                .id(gameLeaderboardDto.getId())
                .lastModifiedDate(gameLeaderboardDto.getLastModifiedDate())
                .user(UserDto.toEntity(gameLeaderboardDto.getUser()))
                .score(gameLeaderboardDto.getScore())
                .rankNumber(gameLeaderboardDto.getRankNumber())
                .date(gameLeaderboardDto.getDate())
                .period(gameLeaderboardDto.getPeriod())
                .build();
    }
}
