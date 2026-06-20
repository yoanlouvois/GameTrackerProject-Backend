package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.UserSanctionApi;
import com.et4.gametrackerproject.dto.UserSanctionDto;
import com.et4.gametrackerproject.enums.SanctionType;
import com.et4.gametrackerproject.services.UserSanctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class UserSanctionController implements UserSanctionApi {

    @Autowired
    private UserSanctionService userSanctionService;

    public UserSanctionController(UserSanctionService userSanctionService) {
        this.userSanctionService = userSanctionService;
    }


    @Override
    public UserSanctionDto applySanction(UserSanctionDto sanctionDto) {
        return userSanctionService.applySanction(sanctionDto);
    }

    @Override
    public UserSanctionDto updateSanction(Integer sanctionId, UserSanctionDto sanctionDto) {
        return userSanctionService.updateSanction(sanctionId, sanctionDto);
    }

    @Override
    public void removeSanction(Integer sanctionId) {
        userSanctionService.deleteSanction(sanctionId);
    }

    @Override
    public UserSanctionDto modifySanctionDuration(Integer sanctionId, Instant newEndDate) {
        return userSanctionService.modifySanctionDuration(sanctionId, newEndDate);
    }

    @Override
    public UserSanctionDto getSanctionById(Integer sanctionId) {
        return userSanctionService.getSanctionById(sanctionId);
    }

    @Override
    public Page<UserSanctionDto> getActiveSanctionsForUser(Integer userId, Pageable pageable) {
        return userSanctionService.getActiveSanctionsForUser(userId, pageable);
    }

    @Override
    public Page<UserSanctionDto> getSanctionsByType(SanctionType type, Pageable pageable) {
        return userSanctionService.getSanctionsByType(type, pageable);
    }

    @Override
    public Page<UserSanctionDto> getSanctionsHistory(Integer userId, Pageable pageable) {
        return userSanctionService.getSanctionsHistory(userId, pageable);
    }

    @Override
    public Integer countActiveSanctions(Integer userId) {
        return userSanctionService.countActiveSanctions(userId);
    }
}
