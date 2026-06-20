package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.Avatar;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AvatarDto {
    private Integer id;

    private String photo;

    @JsonIgnore
    @Builder.Default
    private Set<UserDto> users = new HashSet<>();

    public static AvatarDto fromEntity (Avatar avatar) {
        if(avatar == null){
            return null;
            // TODO throw an exception
        }

        return AvatarDto.builder()
                .id(avatar.getId())
                .photo(avatar.getPhoto())
                .build();
    }

    public static Avatar toEntity (AvatarDto avatarDto) {
        if(avatarDto == null){
            return null;
            // TODO throw an exception
        }

        return Avatar.builder()
                .id(avatarDto.getId())
                .photo(avatarDto.getPhoto())
                .build();
    }
}
