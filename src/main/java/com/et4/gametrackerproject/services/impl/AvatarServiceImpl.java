package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.AvatarDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Avatar;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.model.UserAchievement;
import com.et4.gametrackerproject.repository.AvatarRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.AvatarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.et4.gametrackerproject.exception.ErrorCodes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private static final Logger log = LoggerFactory.getLogger(AvatarServiceImpl.class);
    private final UserRepository userRepository;

    public AvatarServiceImpl(AvatarRepository avatarRepository, UserRepository userRepository) {
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AvatarDto getAvatarById(Integer id) {
        if (id == null) {
            log.error("L'ID de l'avatar est null");
            return null;
        }

        Optional<Avatar> avatar = avatarRepository.findById(id);
        AvatarDto dto = avatar.map(AvatarDto::fromEntity).orElse(null);

        return Optional.ofNullable(dto).orElseThrow(() ->
                new EntityNotFoundException("Aucun avatar trouvé avec l'ID " + id,
                        ErrorCodes.AVATAR_NOT_FOUND)
        );
    }

    @Override
    public List<AvatarDto> getAllAvatars() {
        List<Avatar> avatars = avatarRepository.findAll();
        if (avatars.isEmpty()) {
            log.error("Aucun avatar trouvé");
            throw new EntityNotFoundException("Aucun avatar trouvé", ErrorCodes.AVATAR_NOT_FOUND);
        }
        return avatars.stream()
                .map(AvatarDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public AvatarDto uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("Le fichier d'avatar est null ou vide");
            throw new IllegalArgumentException("Le fichier ne peut être null ou vide");
        }

        Avatar avatar = new Avatar();
        try {
            // Conversion du contenu du fichier en chaîne Base64 pour le stockage dans le champ 'photo'
            String base64Photo = Base64.getEncoder().encodeToString(file.getBytes());
            avatar.setPhoto(base64Photo);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier", e);
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        }

        avatar = avatarRepository.save(avatar);
        return AvatarDto.fromEntity(avatar);
    }

    @Override
    public AvatarDto updateAvatar(Integer id, AvatarDto avatarDto) {
        if (id == null) {
            log.error("L'ID de l'avatar à mettre à jour est null");
            throw new IllegalArgumentException("L'ID de l'avatar ne peut être null");
        }
        if (avatarDto == null) {
            log.error("Les données de mise à jour de l'avatar sont null");
            throw new IllegalArgumentException("Les données de mise à jour de l'avatar ne peuvent être null");
        }

        Optional<Avatar> optionalAvatar = avatarRepository.findById(id);
        if (optionalAvatar.isEmpty()) {
            log.error("Aucun avatar trouvé avec l'ID : " + id);
            throw new EntityNotFoundException("Aucun avatar trouvé avec l'ID " + id, ErrorCodes.AVATAR_NOT_FOUND);
        }

        Avatar existingAvatar = optionalAvatar.get();
        // Mettre à jour les champs pertinents de l'avatar.
        // Ici, nous mettons à jour le champ 'photo'. D'autres champs peuvent être ajoutés selon vos besoins.
        existingAvatar.setPhoto(avatarDto.getPhoto());

        Avatar updatedAvatar = avatarRepository.save(existingAvatar);
        return AvatarDto.fromEntity(updatedAvatar);
    }

    @Override
    public void deleteAvatar(Integer id) {
        if (id == null) {
            log.error("L'ID de l'avatar à supprimer est null");
            throw new IllegalArgumentException("L'ID de l'avatar ne peut être null");
        }

        Optional<Avatar> optionalAvatar = avatarRepository.findById(id);
        if (optionalAvatar.isEmpty()) {
            log.error("Aucun avatar trouvé avec l'ID : " + id);
            throw new EntityNotFoundException("Aucun avatar trouvé avec l'ID " + id, ErrorCodes.AVATAR_NOT_FOUND);
        }

        // Vérification si l'avatar est utilisé par un utilisateur
        Optional<User> users = userRepository.findByAvatarId(id);
        if (users.isPresent()) {
            throw new InvalidOperationException("L'avatar est déjà utilisé par un utilisateur",
                    ErrorCodes.AVATAR_ALREADY_IN_USE);
        }

        avatarRepository.delete(optionalAvatar.get());
    }

    @Override
    public boolean avatarExists(Integer id) {
        if (id == null) {
            log.error("L'ID de l'avatar est null");
            throw new IllegalArgumentException("L'ID de l'avatar ne peut être null");
        }
        return avatarRepository.existsById(id);
    }

    @Override
    public List<AvatarDto> getAllDefaultAvatars() {
        List<Avatar> avatars = avatarRepository.findAll();
        //Les avatars par defaut on id entre 0 et 3 --> choix
        List<Avatar> defaultAvatars = avatars.stream()
                .filter(a -> a.getId() != null && a.getId() >= 0 && a.getId() <= 3)
                .toList();

        if (defaultAvatars.isEmpty()) {
            log.warn("Aucun avatar par défaut trouvé (IDs entre 0 et 3)");
            throw new EntityNotFoundException("Aucun avatar par défaut trouvé", ErrorCodes.AVATAR_NOT_FOUND);
        }

        return defaultAvatars.stream()
                .map(AvatarDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Renvoie la liste des avatars qui ne sont pas utilisés par aucun utilisateur.
     */
    @Override
    public List<AvatarDto> getUnusedAvatars() {
        List<Avatar> unusedAvatars = avatarRepository.findUnusedAvatars();
        if (unusedAvatars.isEmpty()) {
            log.warn("Aucun avatar inutilisé trouvé");
            throw new EntityNotFoundException("Aucun avatar inutilisé trouvé", ErrorCodes.AVATAR_NOT_FOUND);
        }
        return unusedAvatars.stream()
                .map(AvatarDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Renvoie le nombre d'utilisateurs utilisant l'avatar dont l'ID est fourni.
     */
    @Override
    public Long getUserCountByAvatarId(Integer avatarId) {
        if (avatarId == null) {
            log.error("L'ID de l'avatar est null");
            throw new IllegalArgumentException("L'ID de l'avatar ne peut être null");
        }
        return avatarRepository.countUsersByAvatarId(avatarId);
    }

    /**
     * Renvoie la liste des avatars triés par popularité (nombre d'utilisateurs décroissant),
     * encapsulés dans un DTO personnalisé AvatarPopularityDto.
     */
    @Override
    public List<AvatarDto> getMostPopularAvatars() {
        List<Object[]> results = avatarRepository.findAvatarsByPopularity();
        if (results.isEmpty()) {
            log.warn("Aucun avatar populaire trouvé");
            throw new EntityNotFoundException("Aucun avatar populaire trouvé", ErrorCodes.AVATAR_NOT_FOUND);
        }
        return results.stream()
                .map(row -> {
                    Avatar avatar = (Avatar) row[0];
                    return AvatarDto.fromEntity(avatar);
                })
                .collect(Collectors.toList());
    }

}
