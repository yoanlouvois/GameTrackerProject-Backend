package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TagDto {
    private Integer id;

    private String name;

    @JsonIgnore
    @Builder.Default
    private Set<GameTagDto> gameTags = new HashSet<>();

    public static TagDto fromEntity(Tag tag) {
        if(tag == null) {
            return null;
            //TODO: throw exception
        }

        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    public static Tag toEntity(TagDto tagDto) {
        if (tagDto == null) {
            return null;
            //TODO: throw exception
        }

        return Tag.builder()
                .id(tagDto.getId())
                .name(tagDto.getName())
                .build();
    }
}
