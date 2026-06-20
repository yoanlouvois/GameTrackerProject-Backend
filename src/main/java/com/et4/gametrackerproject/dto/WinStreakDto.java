package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.WinStreak;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WinStreakDto {
    private Integer id;

    private Instant lastModifiedDate;

    @JsonIgnore
    private UserDto user;

    private GameDto game;

    @Builder.Default
    private Integer currentStreak = 0;

    @Builder.Default
    private Integer bestStreak = 0;

    private Instant lastWin;

    public static WinStreakDto fromEntity(WinStreak winStreak) {
        if(winStreak == null) {
            return null;
            //TODO: throw exception
        }

        return WinStreakDto.builder()
                .id(winStreak.getId())
                .lastModifiedDate(winStreak.getLastModifiedDate())
                .game(GameDto.fromEntity(winStreak.getGame()))
                .currentStreak(winStreak.getCurrentStreak())
                .bestStreak(winStreak.getBestStreak())
                .lastWin(winStreak.getLastWin())
                .build();
    }

    public static WinStreak toEntity(WinStreakDto winStreakDto) {
        if (winStreakDto == null) {
            return null;
            //TODO: throw exception
        }

        return WinStreak.builder()
                .id(winStreakDto.getId())
                .lastModifiedDate(winStreakDto.getLastModifiedDate())
                .game(GameDto.toEntity(winStreakDto.getGame()))
                .currentStreak(winStreakDto.getCurrentStreak())
                .bestStreak(winStreakDto.getBestStreak())
                .lastWin(winStreakDto.getLastWin())
                .build();
    }
}
