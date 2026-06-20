package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.DailyGameSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DailyGameSessionDto {
    private Integer id;

    @JsonIgnore
    UserDto user;

    private Instant date;

    @Builder.Default
    private Integer totalTimePlayed = 0;

    @Builder.Default
    private Integer gamesPlayed = 0;

    @Builder.Default
    private Integer uniqueGamesPlayed = 0;

    public static DailyGameSessionDto fromEntity (DailyGameSession dailyGameSession) {
        if(dailyGameSession == null){
            return null;
            // TODO throw an exception
        }

        return DailyGameSessionDto.builder()
                .id(dailyGameSession.getId())
                .date(dailyGameSession.getDate())
                .totalTimePlayed(dailyGameSession.getTotalTimePlayed())
                .gamesPlayed(dailyGameSession.getGamesPlayed())
                .uniqueGamesPlayed(dailyGameSession.getUniqueGamesPlayed())
                .build();
    }

    public static DailyGameSession toEntity(DailyGameSessionDto dailyGameSessionDto) {
        if(dailyGameSessionDto == null){
            return null;
            // TODO throw an exception
        }

        return DailyGameSession.builder()
                .id(dailyGameSessionDto.getId())
                .date(dailyGameSessionDto.getDate())
                .totalTimePlayed(dailyGameSessionDto.getTotalTimePlayed())
                .gamesPlayed(dailyGameSessionDto.getGamesPlayed())
                .uniqueGamesPlayed(dailyGameSessionDto.getUniqueGamesPlayed())
                .build();
    }
}
