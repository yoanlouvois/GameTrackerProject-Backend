package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.GameTag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameTagDto {
    private Integer id;

    @JsonIgnore
    private GameDto game;

    private TagDto tag;

    public static GameTagDto fromEntity(GameTag gameTag) {
        if(gameTag == null) {
            return null;
            // TODO: throw exception
        }

        return GameTagDto.builder()
                .id(gameTag.getId())
                .tag(TagDto.fromEntity(gameTag.getTag()))
                .build();
    }

    public static GameTag toEntity(GameTagDto gameTagDto) {
        if (gameTagDto == null) {
            return null;
            // TODO: throw exception
        }

        return GameTag.builder()
                .id(gameTagDto.getId())
                .tag(TagDto.toEntity(gameTagDto.getTag()))
                .build();
    }
}
