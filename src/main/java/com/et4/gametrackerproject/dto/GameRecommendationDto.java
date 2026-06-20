package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.GameRecommendation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameRecommendationDto {
    private Integer id;

    private Instant creationDate;

    @JsonIgnore
    private UserDto sender;

    @JsonIgnore
    private UserDto receiver;

    private GameDto game;

    private String message;

    public static GameRecommendationDto fromEntity(GameRecommendation gameRecommendation) {
        if(gameRecommendation == null) {
            return null;
            // TODO: throw exception
        }

        return GameRecommendationDto.builder()
                .id(gameRecommendation.getId())
                .creationDate(gameRecommendation.getCreationDate())
                .game(GameDto.fromEntity(gameRecommendation.getGame()))
                .message(gameRecommendation.getMessage())
                .build();
    }

    public static GameRecommendation toEntity(GameRecommendationDto gameRecommendationDto) {
        if (gameRecommendationDto == null) {
            return null;
            // TODO: throw exception
        }

        return GameRecommendation.builder()
                .id(gameRecommendationDto.getId())
                .creationDate(gameRecommendationDto.getCreationDate())
                .game(GameDto.toEntity(gameRecommendationDto.getGame()))
                .message(gameRecommendationDto.getMessage())
                .build();
    }
}
