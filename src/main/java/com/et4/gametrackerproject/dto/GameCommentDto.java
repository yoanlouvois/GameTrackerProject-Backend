package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.GameComment;
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
public class GameCommentDto {
    private Integer id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    private UserDto user;

    @JsonIgnore
    private GameDto game;

    private String content;

    @JsonIgnore
    private GameCommentDto parentComment;

    @Builder.Default
    private Set<GameCommentDto> replies = new HashSet<>();

    @Builder.Default
    private Set<GameCommentLikeDto> likes = new HashSet<>();

    public static GameCommentDto fromEntity(GameComment gameComment) {
        if(gameComment == null) {
            return null;
            // TODO: throw exception
        }

        return GameCommentDto.builder()
                .id(gameComment.getId())
                .creationDate(gameComment.getCreationDate())
                .lastModifiedDate(gameComment.getLastModifiedDate())
                .user(UserDto.fromEntity(gameComment.getUser()))
                .content(gameComment.getContent())
                .replies(gameComment.getReplies().stream().map(GameCommentDto::fromEntity).collect(Collectors.toSet()))
                .likes(gameComment.getLikes().stream().map(GameCommentLikeDto::fromEntity).collect(Collectors.toSet()))
                .build();
    }

    public static GameComment toEntity(GameCommentDto gameCommentDto) {
        if (gameCommentDto == null) {
            return null;
            //TODO: throw exception
        }

        return GameComment.builder()
                .id(gameCommentDto.getId())
                .creationDate(gameCommentDto.getCreationDate())
                .lastModifiedDate(gameCommentDto.getLastModifiedDate())
                .user(UserDto.toEntity(gameCommentDto.getUser()))
                .content(gameCommentDto.getContent())
                .replies(gameCommentDto.getReplies().stream().map(GameCommentDto::toEntity).collect(Collectors.toSet()))
                .likes(gameCommentDto.getLikes().stream().map(GameCommentLikeDto::toEntity).collect(Collectors.toSet()))
                .build();
    }
}
