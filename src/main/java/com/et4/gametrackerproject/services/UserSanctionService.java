package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.UserSanctionDto;
import com.et4.gametrackerproject.enums.SanctionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface UserSanctionService {

    // Opérations de base
    UserSanctionDto applySanction(UserSanctionDto sanctionDto);
    UserSanctionDto updateSanction(Integer sanctionId, UserSanctionDto sanctionDto);
    void deleteSanction(Integer sanctionId);

    // Gestion du cycle de vie
    UserSanctionDto modifySanctionDuration(Integer sanctionId, Instant newEndDate);

    // Récupération
    UserSanctionDto getSanctionById(Integer sanctionId);
    Page<UserSanctionDto> getActiveSanctionsForUser(Integer userId, Pageable pageable);
    Page<UserSanctionDto> getSanctionsByType(SanctionType type, Pageable pageable);
    Page<UserSanctionDto> getSanctionsHistory(Integer userId, Pageable pageable);

    // Statistiques
    Integer countActiveSanctions(Integer userId);
}