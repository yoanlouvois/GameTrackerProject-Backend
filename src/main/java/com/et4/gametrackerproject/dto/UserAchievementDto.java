package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.UserAchievement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserAchievementDto {
    private Integer id;

    private Instant creationDate;

    @JsonIgnore
    private UserDto user;

    private AchievementDto achievement;

    @Builder.Default
    private Instant unlockedAt = Instant.now();

    public static UserAchievementDto fromEntity(UserAchievement userAchievement) {
        if(userAchievement == null) {
            return null;
            //TODO: throw exception
        }

        return UserAchievementDto.builder()
                .id(userAchievement.getId())
                .creationDate(userAchievement.getCreationDate())
                .achievement(AchievementDto.fromEntity(userAchievement.getAchievement()))
                .unlockedAt(userAchievement.getUnlockedAt())
                .build();
    }

    public static UserAchievement toEntity(UserAchievementDto userAchievementDto) {
        if (userAchievementDto == null) {
            return null;
            //TODO: throw exception
        }

        return UserAchievement.builder()
                .id(userAchievementDto.getId())
                .creationDate(userAchievementDto.getCreationDate())
                .achievement(AchievementDto.toEntity(userAchievementDto.getAchievement()))
                .unlockedAt(userAchievementDto.getUnlockedAt())
                .build();
    }
}
