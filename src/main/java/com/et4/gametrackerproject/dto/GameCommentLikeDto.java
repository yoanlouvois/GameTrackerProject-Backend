package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.GameCommentLike;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameCommentLikeDto {
    private Integer id;

    private UserDto user;

    @JsonIgnore
    private GameCommentDto comment;

    public static GameCommentLikeDto fromEntity(GameCommentLike gameCommentLike) {
        return GameCommentLikeDto.builder()
                .id(gameCommentLike.getId())
                .user(UserDto.fromEntity(gameCommentLike.getUser()))
                .build();
    }

    public static GameCommentLike toEntity(GameCommentLikeDto gameCommentLikeDto) {
        return GameCommentLike.builder()
                .id(gameCommentLikeDto.getId())
                .user(UserDto.toEntity(gameCommentLikeDto.getUser()))
                .build();
    }
}
