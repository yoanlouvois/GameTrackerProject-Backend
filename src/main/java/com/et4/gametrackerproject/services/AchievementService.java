package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.enums.AchievementRarity;
import com.et4.gametrackerproject.enums.AchievementType;

import java.util.List;

public interface AchievementService {

    //Méthodes de récupération
    List<AchievementDto> getAllAchievements();
    AchievementDto getAchievementById(Integer id);
    List<AchievementDto> getAchievementsByType(AchievementType type);
    List<AchievementDto> getAchievementsByRarity(AchievementRarity rarity);
    List<AchievementDto> getActiveAchievements();


    //Gestion des achievements
    AchievementDto createAchievement(AchievementDto achievementDto);
    AchievementDto updateAchievement(Integer id, AchievementDto achievementDto);

    //Achievement secrets
    List<AchievementDto> getSecretAchievements();
    List<AchievementDto> getAchievementsByDescriptionContaining(String keyword);

    List<AchievementDto> countNumberAchievementsByType();

    void deleteAchievement(Integer id);
}