package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.UserSanctionDto;
import com.et4.gametrackerproject.enums.SanctionType;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidEntityException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.FavoriteGame;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.model.UserSanction;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.repository.UserSanctionRepository;
import com.et4.gametrackerproject.repository.WinStreakRepository;
import com.et4.gametrackerproject.services.UserSanctionService;
import com.et4.gametrackerproject.validator.UserSanctionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UserSanctionServiceImpl implements UserSanctionService {

    private final UserSanctionRepository userSanctionRepository;
    private final UserRepository userRepository;

    public UserSanctionServiceImpl(UserSanctionRepository userSanctionRepository, UserRepository userRepository) {
        this.userSanctionRepository = userSanctionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserSanctionDto applySanction(UserSanctionDto sanctionDto) {
        List<String> errors = UserSanctionValidator.validate(sanctionDto);
        if (!errors.isEmpty()) {
            log.error("Erreur de validation : {}", errors);
            throw new InvalidEntityException("La sanction n'est pas valide", ErrorCodes.USER_SANCTION_NOT_VALID ,errors);
        }

        log.info("Apply sanction {}", sanctionDto);

        return UserSanctionDto.fromEntity(
                userSanctionRepository.save(
                        UserSanctionDto.toEntity(sanctionDto)
                )
        );

    }

    @Override
    public UserSanctionDto updateSanction(Integer sanctionId, UserSanctionDto sanctionDto) {
        List<String> errors = UserSanctionValidator.validate(sanctionDto);
        if (!errors.isEmpty()) {
            log.error("Erreur de validation : {}", errors);
            throw new InvalidEntityException("La sanction n'est pas valide", ErrorCodes.USER_SANCTION_NOT_VALID ,errors);
        }
        if(sanctionId == null || !sanctionId.equals(sanctionDto.getId())){
            log.error("Erreur de validation : L'ID de la sanction doit valide");
            throw new EntityNotFoundException("L'id de la sanction n'existe pas", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }

        log.info("Update sanction {}", sanctionDto);

        return UserSanctionDto.fromEntity(
                userSanctionRepository.save(
                        UserSanctionDto.toEntity(sanctionDto)
                )
        );
    }

    @Override
    public void deleteSanction(Integer sanctionId) {
        if(sanctionId == null){
            log.error("Erreur de validation : L'ID de la sanction doit être valide");
            throw new EntityNotFoundException("L'id est nul", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }
        if(!userSanctionRepository.existsById(sanctionId)){
            log.error("Erreur de validation : Aucune sanction n'a cet ID");
            throw new EntityNotFoundException("Aucune sanction n'a cet ID", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }

        Optional<User> users = userRepository.findByUserSanctionId(sanctionId);
        if (users.isPresent()) {
            log.error("On peut pas supprimer la sanction, l'utilisateur contient des sanctions");
            throw new InvalidOperationException("On peut pas supprimer la sanction, l'utilisateur contient des sanctions",
                    ErrorCodes.USER_SANCTION_ALREADY_USED);
        }
        userSanctionRepository.deleteById(sanctionId);
    }

    @Override
    public UserSanctionDto modifySanctionDuration(Integer sanctionId, Instant newEndDate) {
        if(sanctionId == null){
            log.error("Erreur de validation : L'ID de la sanction doit être valide");
            throw new EntityNotFoundException("L'id est nul", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }
        if(newEndDate == null){
            log.error("Erreur de validation : La date de fin de la sanction doit être valide");
            throw new InvalidEntityException("La date de fin est nulle", ErrorCodes.USER_SANCTION_NOT_VALID);
        }
        if(!userSanctionRepository.existsById(sanctionId)){
            log.error("Erreur de validation : Aucune sanction n'a cet ID");
            throw new EntityNotFoundException("Aucune sanction n'a cet ID", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }
        if(newEndDate.isBefore(Instant.now())){
            log.error("Erreur de validation : La date de fin de la sanction doit être dans le futur");
            throw new InvalidEntityException("La date de fin de la sanction doit être dans le futur", ErrorCodes.USER_SANCTION_NOT_VALID);
        }

        UserSanction sanction = userSanctionRepository.findById(sanctionId).orElseThrow(() ->
                new EntityNotFoundException("Aucune sanction n'a cet ID",
                        ErrorCodes.USER_SANCTION_NOT_FOUND)
        );

        sanction.setEndDate(newEndDate);
        userSanctionRepository.save(sanction);

        log.info("Modify sanction duration with ID {} to {}", sanctionId, newEndDate);

        return UserSanctionDto.fromEntity(sanction);
    }

    @Override
    public UserSanctionDto getSanctionById(Integer sanctionId) {
        if(sanctionId == null){
            log.error("Erreur de validation : L'ID de la sanction doit être valide");
            throw new EntityNotFoundException("L'id est nul", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }

        log.info("Get sanction with ID {}", sanctionId);

        return UserSanctionDto.fromEntity(
                userSanctionRepository.findById(sanctionId).orElseThrow(() ->
                        new EntityNotFoundException("Aucune sanction n'a cet ID",
                                ErrorCodes.USER_SANCTION_NOT_FOUND)
                )
        );
    }

    @Override
    public Page<UserSanctionDto> getActiveSanctionsForUser(Integer userId, Pageable pageable) {
        if(userId == null){
            log.error("Erreur de validation : L'ID de l'utilisateur doit être valide");
            throw new EntityNotFoundException("L'id est nul", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }
        if(!userSanctionRepository.existsById(userId)){
            log.error("Erreur de validation : Aucun utilisateur n'a cet ID");
            throw new EntityNotFoundException("Aucun utilisateur n'a cet ID", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }

        log.info("Get active sanctions for user with ID {}", userId);

        return userSanctionRepository.findByUserId(userId, pageable).map(UserSanctionDto::fromEntity);
    }

    @Override
    public Page<UserSanctionDto> getSanctionsByType(SanctionType type, Pageable pageable) {
        if(type == null){
            log.error("Erreur de validation : Le type de sanction doit être valide");
            throw new InvalidEntityException("Le type de sanction est nul", ErrorCodes.USER_SANCTION_NOT_VALID);
        }

        log.info("Get sanctions by type {}", type);

        return userSanctionRepository.findByType(type, pageable).map(UserSanctionDto::fromEntity);
    }

    @Override
    public Page<UserSanctionDto> getSanctionsHistory(Integer userId, Pageable pageable) {
        if(userId == null){
            log.error("Erreur de validation : L'ID de l'utilisateur doit être valide");
            throw new EntityNotFoundException("L'id est nul", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }
        if(!userSanctionRepository.existsById(userId)){
            log.error("Erreur de validation : Aucun utilisateur n'a cet ID");
            throw new EntityNotFoundException("Aucun utilisateur n'a cet ID", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }

        log.info("Get sanctions history for user with ID {}", userId);

        return userSanctionRepository.findByUserIdOrderByStartDateDesc(userId, pageable).map(UserSanctionDto::fromEntity);
    }

    @Override
    public Integer countActiveSanctions(Integer userId) {
        if(userId == null){
            log.error("Erreur de validation : L'ID de l'utilisateur doit être valide");
            throw new EntityNotFoundException("L'id est nul", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }
        if(!userSanctionRepository.existsById(userId)){
            log.error("Erreur de validation : Aucun utilisateur n'a cet ID");
            throw new EntityNotFoundException("Aucun utilisateur n'a cet ID", ErrorCodes.USER_SANCTION_NOT_FOUND);
        }

        log.info("Count active sanctions for user with ID {}", userId);

        return userSanctionRepository.findActiveByUserId(userId).size();
    }
}
