package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.AchievementRarity;
import com.et4.gametrackerproject.enums.AchievementType;
import com.et4.gametrackerproject.model.Achievement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AchievementDto {
    private Integer id;

    private String name;

    private String description;

    private String icon;

    private Map<String, Object> conditions;

    @Builder.Default
    private Boolean isActive = true;

    private AchievementType type;

    @Builder.Default
    private Integer pointsReward = 0;

    private AchievementRarity rarity;

    private Integer requiresAchievementId;

    @Builder.Default
    private Boolean isSecret = false;

    @JsonIgnore
    @Builder.Default
    private Set<UserAchievementDto> achievementsEarned = new HashSet<>();

    public static AchievementDto fromEntity (Achievement achievement) {
        if(achievement == null){
            return null;
            // TODO throw an exception
        }

        return AchievementDto.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .icon(achievement.getIcon())
                .conditions(achievement.getConditions())
                .isActive(achievement.getIsActive())
                .type(achievement.getType())
                .pointsReward(achievement.getPointsReward())
                .rarity(achievement.getRarity())
                .requiresAchievementId(achievement.getRequiresAchievementId())
                .isSecret(achievement.getIsSecret())
                .build();
    }

    public static Achievement toEntity (AchievementDto achievementDto){
        if(achievementDto == null){
            return null;
            // TODO throw an exception
        }

        return Achievement.builder()
                .id(achievementDto.getId())
                .name(achievementDto.getName())
                .description(achievementDto.getDescription())
                .icon(achievementDto.getIcon())
                .conditions(achievementDto.getConditions())
                .isActive(achievementDto.getIsActive())
                .type(achievementDto.getType())
                .pointsReward(achievementDto.getPointsReward())
                .rarity(achievementDto.getRarity())
                .requiresAchievementId(achievementDto.getRequiresAchievementId())
                .isSecret(achievementDto.getIsSecret())
                .build();
    }
}
