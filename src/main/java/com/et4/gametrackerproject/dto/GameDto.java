package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.DifficultyLevel;
import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.model.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameDto {
    private Integer id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    private String name;

    private String imageUrl;

    private String url;

    private String description;

    private GameCategory category;

    private Double averageRating;

    @Builder.Default
    private Integer playCount = 0;

    @Builder.Default
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    private Integer minAge;

    @Builder.Default
    private Boolean isActive = true;

    @JsonIgnore
    @Builder.Default
    private Set<FavoriteGameDto> favoriteGames = new HashSet<>();

    @Builder.Default
    private Set<GameCommentDto> comments = new HashSet<>();

    @Builder.Default
    private Set<GameLeaderboardDto> leaderboardEntries = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<GameProgressDto> progressions = new HashSet<>();

    @Builder.Default
    private Set<GameRatingDto> ratings = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<GameRecommendationDto> recommendations = new HashSet<>();

    @Builder.Default
    private Set<GameTagDto> tags = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<WinStreakDto> winStreaks = new HashSet<>();

    public static GameDto fromEntity(Game game) {
        if(game == null) {
            return null;
            //TODO: throw exception
        }

        return GameDto.builder()
                .id(game.getId())
                .creationDate(game.getCreationDate())
                .lastModifiedDate(game.getLastModifiedDate())
                .name(game.getName())
                .imageUrl(game.getImageUrl())
                .url(game.getUrl())
                .description(game.getDescription())
                .category(game.getCategory())
                .averageRating(game.getAverageRating())
                .playCount(game.getPlayCount())
                .difficultyLevel(game.getDifficultyLevel())
                .minAge(game.getMinAge())
                .isActive(game.getIsActive())
                .comments(game.getComments().stream().map(GameCommentDto::fromEntity).collect(Collectors.toSet()))
                .leaderboardEntries(game.getLeaderboardEntries().stream().map(GameLeaderboardDto::fromEntity).collect(Collectors.toSet()))
                .ratings(game.getRatings().stream().map(GameRatingDto::fromEntity).collect(Collectors.toSet()))
                .tags(game.getTags().stream().map(GameTagDto::fromEntity).collect(Collectors.toSet()))
                .build();
    }

    public static Game toEntity(GameDto gameDto) {
        if (gameDto == null) {
            return null;
            //TODO: throw exception
        }

        return Game.builder()
                .id(gameDto.getId())
                .creationDate(gameDto.getCreationDate())
                .lastModifiedDate(gameDto.getLastModifiedDate())
                .name(gameDto.getName())
                .url(gameDto.getUrl())
                .imageUrl(gameDto.getImageUrl())
                .description(gameDto.getDescription())
                .category(gameDto.getCategory())
                .averageRating(gameDto.getAverageRating())
                .playCount(gameDto.getPlayCount())
                .difficultyLevel(gameDto.getDifficultyLevel())
                .minAge(gameDto.getMinAge())
                .isActive(gameDto.getIsActive())
                .comments(gameDto.getComments().stream().map(GameCommentDto::toEntity).collect(Collectors.toSet()))
                .leaderboardEntries(gameDto.getLeaderboardEntries().stream().map(GameLeaderboardDto::toEntity).collect(Collectors.toSet()))
                .ratings(gameDto.getRatings().stream().map(GameRatingDto::toEntity).collect(Collectors.toSet()))
                .tags(gameDto.getTags().stream().map(GameTagDto::toEntity).collect(Collectors.toSet()))
                .build();
    }
}
