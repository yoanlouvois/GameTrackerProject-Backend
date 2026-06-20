package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.GameRating;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameRatingDto {
    private Integer id;

    private Instant lastModifiedDate;

    private UserDto user;

    @JsonIgnore
    private GameDto game;

    private Integer rating;

    public static GameRatingDto fromEntity(GameRating gameRating) {
        if(gameRating == null) {
            return null;
            // TODO: throw exception
        }

        return GameRatingDto.builder()
                .id(gameRating.getId())
                .lastModifiedDate(gameRating.getLastModifiedDate())
                .user(UserDto.fromEntity(gameRating.getUser()))
                .rating(gameRating.getRating())
                .build();
    }

    public static GameRating toEntity(GameRatingDto gameRatingDto) {
        if(gameRatingDto == null) {
            return null;
            // TODO: throw exception
        }

        return GameRating.builder()
                .id(gameRatingDto.getId())
                .lastModifiedDate(gameRatingDto.getLastModifiedDate())
                .user(UserDto.toEntity(gameRatingDto.getUser()))
                .rating(gameRatingDto.getRating())
                .build();
    }
}
