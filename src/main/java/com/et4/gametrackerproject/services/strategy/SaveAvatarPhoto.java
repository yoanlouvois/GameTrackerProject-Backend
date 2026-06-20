package com.et4.gametrackerproject.services.strategy;

import com.et4.gametrackerproject.dto.AvatarDto;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.services.AvatarService;
import com.et4.gametrackerproject.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.et4.gametrackerproject.exception.ErrorCodes;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implémentation de la stratégie de sauvegarde pour les photos d'avatar.
 * Cette classe utilise Cloudinary comme service de stockage des images et
 * met à jour les avatars avec les URL d'images correspondantes.
 */
@Service("avatarStrategy")
@Slf4j
public class SaveAvatarPhoto implements Strategy<AvatarDto> {

    private final CloudinaryService cloudinaryService;
    private final AvatarService avatarService;

    /**
     * Constructeur avec injection des dépendances nécessaires.
     *
     * @param cloudinaryService Service pour l'upload et la gestion des images sur Cloudinary
     * @param avatarService Service de gestion des avatars dans l'application
     */
    @Autowired
    public SaveAvatarPhoto(CloudinaryService cloudinaryService, AvatarService avatarService) {
        this.cloudinaryService = cloudinaryService;
        this.avatarService = avatarService;
    }

    /**
     * Sauvegarde une photo d'avatar et associe son URL à l'avatar correspondant.
     *
     * 1. Récupère l'avatar existant par son ID
     * 2. Télécharge la photo vers Cloudinary
     * 3. Vérifie que l'URL retournée est valide
     * 4. Met à jour l'avatar avec l'URL de la photo
     *
     * @param id Identifiant de l'avatar à mettre à jour
     * @param photo Flux d'entrée contenant les données binaires de l'image
     * @param title Titre ou nom à donner à la photo (utilisé comme identifiant dans Cloudinary)
     * @return L'AvatarDto mis à jour avec l'URL de la nouvelle photo
     * @throws IOException Si une erreur se produit pendant la lecture ou l'écriture du fichier
     * @throws InvalidOperationException Si l'enregistrement de la photo échoue
     * @throws EntityNotFoundException Si l'avatar avec l'ID spécifié n'existe pas
     */
    @Override
    public AvatarDto savePhoto(Integer id, InputStream photo, String title) throws IOException {
        log.info("Début de la sauvegarde de la photo pour l'avatar avec l'ID: {}", id);

        // Vérifier si l'ID est valide
        if (id == null) {
            log.error("Impossible de sauvegarder la photo: ID de l'avatar est null");
            throw new InvalidOperationException("ID de l'avatar non fourni", ErrorCodes.INVALID_ID);
        }

        // Récupérer l'avatar existant
        AvatarDto avatar = avatarService.getAvatarById(id);

        log.debug("Avatar récupéré avec succès, upload de la photo vers Cloudinary");

        // Sauvegarder la photo sur Cloudinary et récupérer l'URL
        String urlPhoto = cloudinaryService.savePhoto(photo, title);

        // Vérifier que l'URL retournée est valide
        if (!StringUtils.hasLength(urlPhoto)) {
            log.error("Échec de l'enregistrement de la photo sur Cloudinary pour l'avatar ID: {}", id);
            throw new InvalidOperationException(
                    "Erreur lors de l'enregistrement de la photo de l'avatar",
                    ErrorCodes.UPDATE_PHOTO_EXCEPTION
            );
        }

        log.info("Photo enregistrée avec succès sur Cloudinary, URL: {}", urlPhoto);

        // Mettre à jour l'avatar avec l'URL de la photo
        avatar.setPhoto(urlPhoto);
        AvatarDto updatedAvatar = avatarService.updateAvatar(id, avatar);

        log.info("Avatar mis à jour avec succès avec la nouvelle photo, ID: {}", id);
        return updatedAvatar;
    }
}
