package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.enums.AchievementRarity;
import com.et4.gametrackerproject.enums.AchievementType;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Achievement;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.model.UserAchievement;
import com.et4.gametrackerproject.repository.AchievementRepository;
import com.et4.gametrackerproject.repository.UserAchievementRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.AchievementService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    public AchievementServiceImpl(AchievementRepository achievementRepository, UserRepository userRepository, UserAchievementRepository userAchievementRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    @Override
    public AchievementDto createAchievement(AchievementDto achievementDto) {
        if (achievementDto == null) {
            log.error("L'achievement à créer est null");
            throw new IllegalArgumentException("L'achievement ne peut pas être null");
        }

        Achievement achievement = AchievementDto.toEntity(achievementDto);
        achievement = achievementRepository.save(achievement);

        return AchievementDto.fromEntity(achievement);
    }

    @Override
    public AchievementDto updateAchievement(Integer id, AchievementDto achievementDto) {
        if (id == null) {
            log.error("L'ID de l'achievement à mettre à jour est null");
            throw new IllegalArgumentException("L'ID de l'achievement ne peut pas être null");
        }

        if (achievementDto == null) {
            log.error("Les nouvelles données de l'achievement sont null");
            throw new IllegalArgumentException("Les données de mise à jour ne peuvent pas être null");
        }

        Optional<Achievement> existingAchievement = achievementRepository.findById(id);
        if (existingAchievement.isEmpty()) {
            log.error("Aucun achievement trouvé avec l'ID : " + id);
            throw new EntityNotFoundException("Aucun achievement trouvé avec l'ID " + id,
                    ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        Achievement updatedAchievement = AchievementDto.toEntity(achievementDto);
        updatedAchievement.setId(id); // S'assurer que l'ID reste le même
        updatedAchievement = achievementRepository.save(updatedAchievement);

        return AchievementDto.fromEntity(updatedAchievement);
    }

    @Override
    public void deleteAchievement(Integer id) {
        if (id == null) {
            log.error("L'ID de l'achievement à supprimer est null");
            throw new IllegalArgumentException("L'ID de l'achievement ne peut pas être null");
        }

        // Vérification de l'existence de l'achievement
        Optional<UserAchievement> userAchievements = userAchievementRepository.findAllByAchievementId(id);
        if (userAchievements.isPresent()) {
            throw new InvalidOperationException("Achievement déjà utilisé",
                    ErrorCodes.ACHIEVEMENT_ALREADY_IN_USE);
        }

        achievementRepository.deleteById(id);
    }

    @Override
    public List<AchievementDto> getAllAchievements() {
        List<Achievement> achievements = achievementRepository.findAll();

        if (achievements.isEmpty()) {
            log.error("Aucun achievement trouvé");
            throw new EntityNotFoundException("Aucun achievement trouvé",
                    ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        // Convertir chaque achievement en DTO et les retourner sous forme de liste
        return achievements.stream()
                .map(AchievementDto::fromEntity)
                .collect(Collectors.toList());
    }

    private static final Logger log = LoggerFactory.getLogger(AchievementServiceImpl.class);

    @Override
    public AchievementDto getAchievementById(Integer id) {
        if (id == null){
            log.error("Article ID is null");
            return null;
        }

        Optional<Achievement> achievement = achievementRepository.findById(id);

        AchievementDto dto = AchievementDto.fromEntity(achievement.get());

        return Optional.of(dto).orElseThrow(() ->
        new EntityNotFoundException("Aucun article avec l'ID "+ id + "Trouvée",
                ErrorCodes.ACHIEVEMENT_NOT_FOUND)
                );
    }

    @Override
    public List<AchievementDto> getAchievementsByType(AchievementType type) {
        if (type == null) {
            log.error("Le type d'achievement est null");
            throw new IllegalArgumentException("Le type d'achievement ne peut pas être null");
        }

        List<Achievement> achievements = achievementRepository.findByType(type);

        if (achievements.isEmpty()) {
            log.warn("Aucun achievement trouvé pour le type : " + type);
            throw new EntityNotFoundException("Aucun achievement trouvé pour le type : " + type,
                    ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        return achievements.stream()
                .map(AchievementDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementDto> getAchievementsByRarity(AchievementRarity rarity) {
        if (rarity == null) {
            log.error("La rareté de l'achievement est null");
            throw new IllegalArgumentException("La rareté de l'achievement ne peut pas être null");
        }

        List<Achievement> achievements = achievementRepository.findByRarity(rarity);

        if (achievements.isEmpty()) {
            log.warn("Aucun achievement trouvé pour la rareté : " + rarity);
            throw new EntityNotFoundException("Aucun achievement trouvé pour la rareté : " + rarity,
                    ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        return achievements.stream()
                .map(AchievementDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementDto> getActiveAchievements() {
        List<Achievement> achievements = achievementRepository.findByIsActiveTrue();

        if (achievements.isEmpty()) {
            log.warn("Aucun achievement actif trouvé");
            throw new EntityNotFoundException("Aucun achievement actif trouvé",
                    ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        return achievements.stream()
                .map(AchievementDto::fromEntity)
                .collect(Collectors.toList());
    }



    @Override
    public List<AchievementDto> getSecretAchievements() {
        List<Achievement> achievements = achievementRepository.findByIsSecretTrue();

        if (achievements.isEmpty()) {
            log.warn("Aucun achievement secret trouvé");
            throw new EntityNotFoundException("Aucun achievement actif trouvé",
                    ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        return achievements.stream()
                .map(AchievementDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementDto> getAchievementsByDescriptionContaining(String keyword){
        List<Achievement> achievements = achievementRepository.findByDescriptionContaining(keyword);

        if (achievements.isEmpty()) {
            log.warn("Aucun achievement par description trouvé");
            throw new EntityNotFoundException("Aucun achievement actif trouvé",
                    ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        return achievements.stream()
                .map(AchievementDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementDto> countNumberAchievementsByType() {
        // Récupère la liste d'objets Object[] où chaque élément contient [AchievementType, Long count]
        List<Object[]> results = achievementRepository.countByType();

        // Transformation des résultats en une liste de AchievementDto
        return results.stream()
                .map(row -> {
                    AchievementType type = (AchievementType) row[0];
                    Long count = (Long) row[1];
                    // Ici, on utilise le champ pointsReward pour stocker le compte, à adapter selon ton besoin
                    return AchievementDto.builder()
                            .type(type)
                            .pointsReward(count.intValue())
                            .build();
                })
                .collect(Collectors.toList());
    }



}

