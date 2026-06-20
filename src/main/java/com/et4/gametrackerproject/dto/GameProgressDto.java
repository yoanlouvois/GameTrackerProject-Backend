package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.GameStatus;
import com.et4.gametrackerproject.model.GameProgress;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameProgressDto {
    private Integer id;

    private Instant creationDate;

    @JsonIgnore
    private UserDto user;

    private GameDto game;

    @Builder.Default
    private GameStatus status = GameStatus.IN_PROGRESS;

    private Integer score;

    private Instant lastPlayed;

    private String progressData; // NE PAS CONVERTIR car ça va être stocké et réutilisé comme tel

    @Builder.Default
    private Integer timePlayed = 0;

    @Builder.Default
    private Integer attempts = 0;

    @Builder.Default
    private Integer wins = 0;

    @Builder.Default
    private Integer losses = 0;

    private Integer bestScore;

    @Builder.Default
    private Integer currentStreak = 0;

    public static GameProgressDto fromEntity(GameProgress progress) {
        if(progress == null) {
            return null;
            // TODO: throw exception
        }

        return GameProgressDto.builder()
                .id(progress.getId())
                .creationDate(progress.getCreationDate())
                .game(GameDto.fromEntity(progress.getGame()))
                .status(progress.getStatus())
                .score(progress.getScore())
                .lastPlayed(progress.getLastPlayed())
                .progressData(progress.getProgressData())
                .timePlayed(progress.getTimePlayed())
                .attempts(progress.getAttempts())
                .wins(progress.getWins())
                .losses(progress.getLosses())
                .bestScore(progress.getBestScore())
                .currentStreak(progress.getCurrentStreak())
                .build();
    }

    public static GameProgress toEntity(GameProgressDto progressDto) {
        if (progressDto == null) {
            return null;
            // TODO: throw exception
        }

        return GameProgress.builder()
                .id(progressDto.getId())
                .creationDate(progressDto.getCreationDate())
                .game(GameDto.toEntity(progressDto.getGame()))
                .status(progressDto.getStatus())
                .score(progressDto.getScore())
                .lastPlayed(progressDto.getLastPlayed())
                .progressData(progressDto.getProgressData())
                .timePlayed(progressDto.getTimePlayed())
                .attempts(progressDto.getAttempts())
                .wins(progressDto.getWins())
                .losses(progressDto.getLosses())
                .bestScore(progressDto.getBestScore())
                .currentStreak(progressDto.getCurrentStreak())
                .build();

    }
}
