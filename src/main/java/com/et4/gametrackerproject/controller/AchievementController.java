package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.AchievementApi;
import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.enums.AchievementRarity;
import com.et4.gametrackerproject.enums.AchievementType;
import com.et4.gametrackerproject.services.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;



@RestController
public class AchievementController implements AchievementApi {

    @Autowired
    private AchievementService achievementService ;

    public AchievementController(AchievementService achievementservice) {
        this.achievementService = achievementservice;
    }

    @Override
    public AchievementDto createAchievement(AchievementDto achievementDto) {
        return achievementService.createAchievement(achievementDto);
    }

    @Override
    public AchievementDto updateAchievement(Integer id, AchievementDto achievementDto) {
        return achievementService.updateAchievement(id, achievementDto);
    }

    @Override
    public void deleteAchievement(Integer id) {
        achievementService.deleteAchievement(id);
    }
    @Override
    public List<AchievementDto> getAllAchievements() {
        return achievementService.getAllAchievements();
    }

    @Override
    public AchievementDto getAchievementById(Integer id) {
        return achievementService.getAchievementById(id);
    }

    @Override
    public List<AchievementDto> getAchievementsByType(AchievementType type) {
        return achievementService.getAchievementsByType(type);
    }

    @Override
    public List<AchievementDto> getAchievementsByRarity(AchievementRarity rarity) {
        return achievementService.getAchievementsByRarity(rarity);
    }

    @Override
    public List<AchievementDto> getActiveAchievements() {
        return achievementService.getActiveAchievements();
    }




    @Override
    public List<AchievementDto> getSecretAchievements() {
        return achievementService.getSecretAchievements();
    }

    @Override
    public List<AchievementDto> getAchievementsByDescriptionContaining(String keyword) {
        return achievementService.getAchievementsByDescriptionContaining(keyword);
    }

    @Override
    public List<AchievementDto> countNumberAchievementsByType() {
        return achievementService.countNumberAchievementsByType();
    }
}
