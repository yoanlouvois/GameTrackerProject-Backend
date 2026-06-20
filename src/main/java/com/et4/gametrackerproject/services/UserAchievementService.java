package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.UserAchievementDto;
import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.model.Achievement;
import com.et4.gametrackerproject.model.UserAchievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserAchievementService {

    //Opérations de base
    UserAchievementDto unlockAchievement(UserAchievementDto userAchievementDto);
    void deleteUserAchievement(Integer userAchievementId);

    // Récupération
    UserAchievementDto getUserAchievementById(Integer userAchievementId);
    Page<UserAchievementDto> getAchievementsByUser(Integer userId, Pageable pageable);
    Set<UserAchievementDto> getRecentUnlocks(Integer userId, int days);

    // Vérifications
    boolean hasAchievement(Integer userId, Integer achievementId);
    boolean hasAllPrerequisites(Integer userId, Integer achievementId);
    Map<AchievementDto, Boolean> getAchievementProgress(Integer userId);

    // Statistiques
    Integer getTotalAchievementPoints(Integer userId);
    Double getGlobalUnlockRate(Integer achievementId);
}