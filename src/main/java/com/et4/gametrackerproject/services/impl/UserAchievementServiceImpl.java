package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.dto.UserAchievementDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidEntityException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Achievement;
import com.et4.gametrackerproject.model.GameTag;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.model.UserAchievement;
import com.et4.gametrackerproject.repository.AchievementRepository;
import com.et4.gametrackerproject.repository.UserAchievementRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.UserAchievementService;
import com.et4.gametrackerproject.validator.UserAchievementValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAchievementServiceImpl implements UserAchievementService {

    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    @Autowired
    public UserAchievementServiceImpl(UserAchievementRepository userAchievementRepository, UserRepository userRepository, AchievementRepository achievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    @Override
    public UserAchievementDto unlockAchievement(UserAchievementDto userAchievementDto) {
        List<String> errors = UserAchievementValidator.validate(userAchievementDto);
        if (errors.isEmpty()) {
            log.error("User is not valid : {}", errors);
            throw new InvalidEntityException("User is not valid", ErrorCodes.USER_ACHIEVEMENT_NOT_VALID, errors);
        }

        log.info("Unlocking achievement : {}", userAchievementDto);

        return UserAchievementDto.fromEntity(userAchievementRepository.save(UserAchievementDto.toEntity(userAchievementDto)));
    }

    @Override
    public void deleteUserAchievement(Integer userAchievementId) {
        if(userAchievementId == null){
            log.error("UserAchievement id is null");
            throw new EntityNotFoundException("No userAchievement found with id : " + userAchievementId, ErrorCodes.USER_ACHIEVEMENT_NOT_FOUND);
        }
        if(!userAchievementRepository.existsById(userAchievementId)){
            log.error("UserAchievement with id : {} is not found", userAchievementId);
            throw new EntityNotFoundException("No userAchievement found with id : " + userAchievementId, ErrorCodes.USER_ACHIEVEMENT_NOT_FOUND);
        }

        Optional<User> users = userRepository.findByUserAchievementId(userAchievementId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer le UserAchievement avec l'ID {} car il est référencé par un user", userAchievementId);
            throw new InvalidOperationException("Impossible de supprimer le UserAchievement car il est référencé par un user",
                    ErrorCodes.TAG_ALREADY_USED);
        }

        Optional<Achievement> achievements = achievementRepository.findByUserAchievementId(userAchievementId);
        if (achievements.isPresent()) {
            log.error("Impossible de supprimer le UserAchievement avec l'ID {} car il est référencé par un achievement", userAchievementId);
            throw new InvalidOperationException("Impossible de supprimer le UserAchievement car il est référencé par un achievement",
                    ErrorCodes.TAG_ALREADY_USED);
        }

        userAchievementRepository.deleteById(userAchievementId);
    }

    @Override
    public UserAchievementDto getUserAchievementById(Integer userAchievementId) {
        if(userAchievementId == null){
            log.error("UserAchievement id is null");
            throw new EntityNotFoundException("No userAchievement found with id : " + userAchievementId, ErrorCodes.USER_ACHIEVEMENT_NOT_FOUND);
        }

        log.info("Fetching userAchievement with id : {}", userAchievementId);

        return userAchievementRepository.findById(userAchievementId).map(UserAchievementDto::fromEntity).orElseThrow(() ->
                new EntityNotFoundException("No userAchievement found with id : " + userAchievementId, ErrorCodes.USER_ACHIEVEMENT_NOT_FOUND));
    }

    @Override
    public Page<UserAchievementDto> getAchievementsByUser(Integer userId, Pageable pageable) {
        if(userId == null){
            log.error("User id is null");
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Fetching achievements for user with id : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND));

        return userAchievementRepository.findByUser(user, pageable).map(UserAchievementDto::fromEntity);
    }

    @Override
    public Set<UserAchievementDto> getRecentUnlocks(Integer userId, int days) {
        if(userId == null){
            log.error("User id is null");
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Fetching recent unlocks for user with id : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND));

        Instant start = Instant.now().minusSeconds(days * 24 * 60 * 60);
        Instant end = Instant.now();

        List<UserAchievement> userAchievements = userAchievementRepository.findByUserAndUnlockedAtBetween(user, start, end);

        return userAchievements.stream().map(UserAchievementDto::fromEntity).collect(Collectors.toSet());
    }

    @Override
    public boolean hasAchievement(Integer userId, Integer achievementId) {
        if(userId == null){
            log.error("User id is null");
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }
        if(achievementId == null){
            log.error("Achievement id is null");
            throw new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)){
            log.error("User with id : {} is not found", userId);
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }
        if(!achievementRepository.existsById(achievementId)){
            log.error("Achievement with id : {} is not found", achievementId);
            throw new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        log.info("Checking if user with id : {} has achievement with id : {}", userId, achievementId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND));

        Achievement achievement = achievementRepository.findById(achievementId).orElseThrow(() ->
                new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND));

        return userAchievementRepository.existsByUserAndAchievement(user, achievement);
    }

    @Override
    public boolean hasAllPrerequisites(Integer userId, Integer achievementId) {
        if(userId == null){
            log.error("User id is null");
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }
        if(achievementId == null){
            log.error("Achievement id is null");
            throw new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)){
            log.error("User with id : {} is not found", userId);
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }
        if(!achievementRepository.existsById(achievementId)){
            log.error("Achievement with id : {} is not found", achievementId);
            throw new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        log.info("Checking if user with id : {} has all prerequisites for achievement with id : {}", userId, achievementId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND));

        Achievement achievement = achievementRepository.findById(achievementId).orElseThrow(() ->
                new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND));

        // TODO : Implémenter la vérification des dépendances
        return false;
    }

    @Override
    public Map<AchievementDto, Boolean> getAchievementProgress(Integer userId) {
        if(userId == null){
            log.error("User id is null");
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)){
            log.error("User with id : {} is not found", userId);
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Fetching achievement progress for user with id : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND));

        // TODO : Implémenter la récupération de la progression des succès
        return Map.of();
    }

    @Override
    public Integer getTotalAchievementPoints(Integer userId) {
        if(userId == null){
            log.error("User id is null");
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)){
            log.error("User with id : {} is not found", userId);
            throw new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Fetching total achievement points for user with id : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("No user found with id : " + userId, ErrorCodes.USER_NOT_FOUND));

        return userAchievementRepository.countAchievementsUnlockedByUser(user).intValue();
    }

    @Override
    public Double getGlobalUnlockRate(Integer achievementId) {
        if (achievementId == null) {
            log.error("Achievement id is null");
            throw new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }
        if (!achievementRepository.existsById(achievementId)) {
            log.error("Achievement with id : {} is not found", achievementId);
            throw new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND);
        }

        log.info("Calculating global unlock rate for achievement with id : {}", achievementId);

        Achievement achievement = achievementRepository.findById(achievementId).orElseThrow(() ->
                new EntityNotFoundException("No achievement found with id : " + achievementId, ErrorCodes.ACHIEVEMENT_NOT_FOUND));

        return userAchievementRepository.calculateGlobalUnlockRate(achievement);
    }

}
